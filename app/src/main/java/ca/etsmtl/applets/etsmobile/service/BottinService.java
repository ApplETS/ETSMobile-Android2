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

    private Intent broadcastIntent;

    public BottinService() {
        super("BottinService");
    }

    public BottinService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
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

        // Envoi d'un intent incluant une exception à BottinFragment
        sendBroadcast(broadcastIntent);
    }

    @Override
    public void onRequestSuccess(final Object o) {
        if (o instanceof HashMap<?, ?>) {

            new AsyncTask<Void, Void, Void>() {
                @SuppressWarnings("unchecked")
                HashMap<String, List<FicheEmploye>> listeEmployeByService = (HashMap<String, List<FicheEmploye>>) o;

                @Override
                protected Void doInBackground(Void... params) {
                    updateDb(listeEmployeByService);

                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);

                    // Envoi d'un intent à BottinFragment
                    sendBroadcast(broadcastIntent);
                }
            }.execute();
        }
    }
}
