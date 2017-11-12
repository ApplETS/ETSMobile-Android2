package ca.etsmtl.applets.etsmobile.service;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.j256.ormlite.dao.Dao;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.db.DatabaseHelper;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.model.FicheEmploye;
import ca.etsmtl.applets.etsmobile.ui.fragment.BottinFragment;

/**
 * Created by Sonphil on 23-06-17.
 */

public class BottinSyncJob extends Job {

    public static final String TAG = "bottin_sync_job_tag";

    public static boolean syncEnCours;
    private static boolean syncReussie;

    private RequestListener<Object> requestListener;
    private CountDownLatch countDownLatch;
    private Intent broadcastIntent;

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        syncEnCours = true;

        if (broadcastIntent == null)
            createBroadcastIntent();

        if (requestListener == null)
            createRequestListener();

        countDownLatch = new CountDownLatch(1);

        rechargerBottin();

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();

            return Result.FAILURE;
        }

        if (syncReussie) {
            Log.d(TAG, "Fin. Envoi d'un BroadcastIntent");
            getContext().sendBroadcast(broadcastIntent);

            return Result.SUCCESS;
        } else
            return Result.FAILURE;
    }

    /**
     * Schedule a job if the job hasn't been already scheduled
     * Returns the jobId. Returns -1 if no job has been created.
     *
     * @return the jobId
     */
    public static int scheduleJob() {
        int jobId = -1;

        Set<JobRequest> jobRequests = JobManager.instance().getAllJobRequestsForTag(TAG);

        if (jobRequests.isEmpty()) {
           jobId = new JobRequest.Builder(TAG)
                    .setPeriodic(TimeUnit.DAYS.toMillis(1))
                    .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .setRequirementsEnforced(true)
                    .setUpdateCurrent(true)
                    .build()
                    .schedule();
        } else {
            for (JobRequest jobRequest : jobRequests) {
                Log.d(TAG, "Job already scheduled! JobId: " + jobRequest.getJobId()
                        + ". Interval (ms): " + jobRequest.getIntervalMs() + ". Last run: "
                        + jobRequest.getLastRun());
            }
        }

        return jobId;
    }

    private void createRequestListener() {
        requestListener = new RequestListener<Object>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.d(TAG, "Échec de la requête");

                Bundle extras = new Bundle();
                extras.putSerializable(BottinFragment.BottinFragmentReceiver.EXCEPTION, spiceException);
                broadcastIntent.putExtras(extras);

                syncEnCours = false;
                syncReussie = false;

                // Envoi d'un intent incluant une exception à BottinFragment
                getContext().sendBroadcast(broadcastIntent);

                countDownLatch.countDown();
            }

            @Override
            public void onRequestSuccess(@NonNull final Object o) {
                Log.d(TAG, "Succès de la requête");

                if (o instanceof HashMap<?, ?>) {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();

                            updateDb((HashMap<String, List<FicheEmploye>>) o);

                            syncEnCours = false;
                            syncReussie = true;

                            Log.d(TAG, "Fin de la maj de la BD");

                            countDownLatch.countDown();
                        }
                    }.start();
                }
            }
        };
    }

    private void createBroadcastIntent() {
        broadcastIntent = new Intent();
        broadcastIntent.setAction(BottinFragment.BottinFragmentReceiver.ACTION_SYNC_BOTTIN);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
    }

    private void rechargerBottin() {

        Log.d(TAG, "Requête");

        try {
            DataManager datamanager = DataManager.getInstance(getContext().getApplicationContext());
            datamanager.getDataFromSignet(DataManager.SignetMethods.BOTTIN_GET_LIST_SERVICE_AND_EMP,
                    ApplicationManager.userCredentials, requestListener);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private void updateDb(final HashMap<String, List<FicheEmploye>> listeEmployeByService) {
        Log.d(TAG, "Mise à jour de la BD");

        DatabaseHelper dbHelper = new DatabaseHelper(getContext());

        try {
            Dao<FicheEmploye, ?> ficheEmployeDao = dbHelper.getDao(FicheEmploye.class);

            for (FicheEmploye ficheEmploye : ficheEmployeDao.queryForAll()) {
                ficheEmployeDao.delete(ficheEmploye);
            }

            for (String nomService : listeEmployeByService.keySet()) {

                List<FicheEmploye> listeEmployes = listeEmployeByService.get(nomService);

                if (listeEmployes.size() > 0) {
                    for (FicheEmploye ficheEmploye : listeEmployeByService.get(nomService)) {
                        dbHelper.getDao(FicheEmploye.class).create(ficheEmploye);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(DatabaseHelper.class.getName(), "SQLException", e);
            throw new RuntimeException(e);
        }
    }
}
