package ca.etsmtl.applets.etsmobile.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
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
import ca.etsmtl.applets.etsmobile.ui.fragment.BottinFragment;

/**
 * Created by Sonphil on 17-06-17.
 */

public class BottinService extends IntentService implements RequestListener<Object> {

    public BottinService() {
        super("BottinService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public BottinService(String name) {
        super(name);
    }

    /**
     * This method is invoked on the worker thread with a request to process.
     * Only one Intent is processed at a time, but the processing happens on a
     * worker thread that runs independently from other application logic.
     * So, if this code takes a long time, it will hold up other requests to
     * the same IntentService, but it will not hold up anything else.
     * When all requests have been handled, the IntentService stops itself,
     * so you should not call {@link #stopSelf}.
     *
     * @param intent The value passed to {@link
     *               Context#startService(Intent)}.
     *               This may be null if the service is being restarted after
     *               its process has gone away; see
     *               {@link Service#onStartCommand}
     *               for details.
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
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

    }

    @Override
    public void onRequestSuccess(Object o) {
        if (o instanceof HashMap<?, ?>) {
            @SuppressWarnings("unchecked")
            HashMap<String, List<FicheEmploye>> listeEmployeByService = (HashMap<String, List<FicheEmploye>>) o;

            updateDb(listeEmployeByService);

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(BottinFragment.BottinReceiver.ACTION_SYNC_BOTTIN);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            sendBroadcast(broadcastIntent);
        }
    }
}
