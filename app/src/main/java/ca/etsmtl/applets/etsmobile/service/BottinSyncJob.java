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

    private static final int NB_VERIFICATIONS = 3;
    private static final int DELAI_ENTRE_CHAQUE_VERIFICATION_EN_MS = 10000;

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        /*
         Si la synchronisation est en cours, cela signifie que l'utilisateur a démarré le service
         manuellement.
          */
        if (!BottinService.syncEnCours) {
            // Lancement du service permettant de synchroniser le bottin
            Intent intent = new Intent(getContext(), BottinService.class);
            getContext().startService(intent);
        }

        for (int i = 0; i < NB_VERIFICATIONS; i++) {
            try {
                // Attente
                Thread.sleep(DELAI_ENTRE_CHAQUE_VERIFICATION_EN_MS);
                // Vérification de la fin de la synchronisation ainsi que de sa réussite
                if (!BottinService.syncEnCours && BottinService.syncReussie) {
                    return Result.SUCCESS;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Toutes les vérifications ont échouées
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
