package ca.etsmtl.applets.etsmobile.service;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import java.util.concurrent.TimeUnit;

/**
 * Created by Sonphil on 23-06-17.
 */

public class BottinSyncJob extends Job {

    public static final String TAG = "bottin_sync_job_tag";

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        /*
         Si la synchronisation est en cours, cela signifie que l'utilisateur a démarré le service
         manuellement.
          */
        if (!BottinService.isSyncEnCours()) {
            // Lancement du service permettant de synchroniser le bottin
            Intent intent = new Intent(getContext(), BottinService.class);
            getContext().startService(intent);
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while(BottinService.isSyncEnCours());

        if (BottinService.isSyncReussie())
            return Result.SUCCESS;
        else
            return Result.FAILURE;
    }

    public static int scheduleJob() {
        int jobId = new JobRequest.Builder(TAG)
                .setPeriodic(TimeUnit.DAYS.toMillis(1))
                .setPersisted(true)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .setRequirementsEnforced(true)
                .setUpdateCurrent(true)
                .build()
                .schedule();

        return jobId;
    }
}
