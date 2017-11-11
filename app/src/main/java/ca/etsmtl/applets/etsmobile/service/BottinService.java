package ca.etsmtl.applets.etsmobile.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.lang.ref.WeakReference;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.db.DatabaseHelper;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.model.FicheEmploye;
import ca.etsmtl.applets.etsmobile.ui.fragment.BottinFragment.BottinFragmentReceiver;

/**
 * <h1>BottinService</h1>
 * <p>
 * Synchronisation du bottin en effectuant une requête des données distantes via le
 * {@link DataManager} et en mettant à jour de la BD
 * <p>
 * Created by Sonphil on 17-06-17.
 */

public class BottinService extends IntentService implements RequestListener<Object> {

    private static final String TAG = "Bottin_Service";

    private static boolean syncEnCours;
    private static boolean syncReussie;

    private Intent broadcastIntent;

    public BottinService() {
        super("BottinService");
    }

    public BottinService(String name) {
        super(name);
    }

    public static boolean isSyncEnCours() {
        return syncEnCours;
    }

    public static boolean isSyncReussie() {
        return syncReussie;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        syncEnCours = true;
        syncReussie = false;

        broadcastIntent = new Intent();
        broadcastIntent.setAction(BottinFragmentReceiver.ACTION_SYNC_BOTTIN);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);

        rechargerBottin();
    }

    private void rechargerBottin() {
        try {
            DataManager datamanager = DataManager.getInstance(this);
            datamanager.getDataFromSignet(DataManager.SignetMethods.BOTTIN_GET_LIST_SERVICE_AND_EMP,
                    ApplicationManager.userCredentials, this);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private void updateDb(final HashMap<String, List<FicheEmploye>> listeEmployeByService) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);

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

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        Bundle extras = new Bundle();
        extras.putSerializable(BottinFragmentReceiver.EXCEPTION, spiceException);
        broadcastIntent.putExtras(extras);

        syncEnCours = false;
        syncReussie = false;

        // Envoi d'un intent incluant une exception à BottinFragment
        sendBroadcast(broadcastIntent);
    }

    @Override
    public void onRequestSuccess(final Object o) {
        if (o instanceof HashMap<?, ?>)
            new BottinAsyncTask(this, (HashMap<String, List<FicheEmploye>>) o).execute();
    }

    private static class BottinAsyncTask extends AsyncTask<Void, Void, Void> {

        private final HashMap<String, List<FicheEmploye>> listeEmployeByService;
        private final WeakReference<BottinService> service;

        BottinAsyncTask(BottinService service, HashMap<String, List<FicheEmploye>> listeEmployeByService) {
            this.listeEmployeByService = listeEmployeByService;
            this.service = new WeakReference<>(service);
        }

        @Override
        protected Void doInBackground(Void... params) {
            service.get().updateDb(listeEmployeByService);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            syncEnCours = false;
            syncReussie = true;

            // Envoi d'un intent à BottinFragment
            service.get().sendBroadcast(service.get().broadcastIntent);
        }
    }
}
