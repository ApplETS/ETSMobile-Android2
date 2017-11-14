package ca.etsmtl.applets.etsmobile.service;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import ca.etsmtl.applets.etsmobile.db.DatabaseHelper;
import ca.etsmtl.applets.etsmobile.http.soap.WebServiceSoap;
import ca.etsmtl.applets.etsmobile.model.ArrayOfFicheEmploye;
import ca.etsmtl.applets.etsmobile.model.ArrayOfService;
import ca.etsmtl.applets.etsmobile.model.FicheEmploye;
import ca.etsmtl.applets.etsmobile.model.Service;
import ca.etsmtl.applets.etsmobile.ui.fragment.BottinFragment;

/**
 * Created by Sonphil on 23-06-17.
 */

public class BottinSyncJob extends Job {

    public static final String TAG = "bottin_sync_job_tag";

    private static boolean syncEnCours;
    private static boolean syncReussie;

    private CountDownLatch countDownLatch;
    private Intent broadcastIntent;

    public static boolean isSyncEnCours() {
        return syncEnCours;
    }

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        syncEnCours = true;

        if (broadcastIntent == null)
            createBroadcastIntent();
        
        countDownLatch = new CountDownLatch(1);

        rechargerBottin();

        try {
            // Attente de la fin du rechargement du bottin
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

    private void createBroadcastIntent() {
        broadcastIntent = new Intent();
        broadcastIntent.setAction(BottinFragment.BottinFragmentReceiver.ACTION_SYNC_BOTTIN);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
    }

    private void rechargerBottin() {

        new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    Log.d(TAG, "Requête des données distantes");

                    HashMap<String, List<FicheEmploye>> listeEmployeByService = getListeEmployeByService();
                    if (listeEmployeByService != null && listeEmployeByService.size() > 0) {
                        updateDb(listeEmployeByService);

                        syncEnCours = false;
                        syncReussie = true;

                        Log.d(TAG, "Fin de la maj de la BD");

                        countDownLatch.countDown();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    handleRequestFailure(e);
                }
            }
        }.start();
    }

    private void handleRequestFailure(Exception exception) {
        Log.d(TAG, "Échec de la requête");

        Bundle extras = new Bundle();
        extras.putSerializable(BottinFragment.BottinFragmentReceiver.EXCEPTION, exception);
        broadcastIntent.putExtras(extras);

        syncEnCours = false;
        syncReussie = false;

        // Envoi d'un intent incluant une exception à BottinFragment
        getContext().sendBroadcast(broadcastIntent);

        countDownLatch.countDown();
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

    private HashMap<String, List<FicheEmploye>> getListeEmployeByService() throws Exception {
        ArrayOfService arrayOfService = new WebServiceSoap().GetListeDepartement();

        HashMap<String, List<FicheEmploye>> listeEmployeByService = new HashMap<String, List<FicheEmploye>>();
        ArrayOfFicheEmploye arrayOfFicheEmploye;

        for (int i = 0; i < arrayOfService.size(); i++) {

            Service service = arrayOfService.get(i);
            arrayOfFicheEmploye = new WebServiceSoap().Recherche(null, null, "" + service.ServiceCode);

            listeEmployeByService.put(service.Nom, arrayOfFicheEmploye);

        }

        return listeEmployeByService;
    }
}
