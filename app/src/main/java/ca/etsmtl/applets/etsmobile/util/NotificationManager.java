package ca.etsmtl.applets.etsmobile.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.octo.android.robospice.request.listener.RequestListener;

import java.lang.ref.WeakReference;
import java.sql.SQLException;
import java.util.List;

import ca.etsmtl.applets.etsmobile.db.DatabaseHelper;
import ca.etsmtl.applets.etsmobile.http.MonETSWebService;
import ca.etsmtl.applets.etsmobile.model.MonETSNotification;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

/**
 * Class used for managing notifications for {@link ca.etsmtl.applets.etsmobile.ui.activity.NotificationActivity}
 * @author zaclimon
 */
public class NotificationManager {

    private Context context;
    private MonETSWebService service;
    private RequestListener<Object> listener;

    /**
     * Default constructor for a manager
     *
     * @param managerContext The context required for the manager to operate
     * @param managerService The {@link MonETSWebService} required to communicate with the school API's
     * @param managerListener the listener used when an action for the manager is done.
     */
    public NotificationManager(Context managerContext, MonETSWebService managerService, RequestListener<Object> managerListener) {
        context = managerContext;
        service = managerService;
        listener = managerListener;
    }

    /**
     * Fetches notifications coming from the school and saves them in the internal app's database
     */
    public void updateNotifications() {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);

        if (accounts.length > 0) {
            String authToken = accountManager.peekAuthToken(accounts[0], Constants.AUTH_TOKEN_TYPE);
            Call<List<MonETSNotification>> notificationCall = service.getAllNotifications(authToken);
            notificationCall.enqueue(createCallback());
        }
    }

    /**
     * Creates a callback after a call to the MonÉTS API has been made. If the response is successful,
     * an update to the database will be made.
     *
     * @return the callback for the notification {@link Call}
     */
    private Callback<List<MonETSNotification>> createCallback() {
        return new Callback<List<MonETSNotification>>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<List<MonETSNotification>> call, Response<List<MonETSNotification>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateDatabase(response.body());
                } else {
                    Log.e("NotificationManager", "Error code: " + response.code());
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<List<MonETSNotification>> call, Throwable t) {
                t.printStackTrace();
                new AsyncUpdateMonETSToken(context, NotificationManager.this).execute();
            }
        };
    }

    /**
     * Updates the database with a given list of notifications
     *
     * @param notifications the list of notifications that have been fetched from MonÉTS's API
     */
    private void updateDatabase(List<MonETSNotification> notifications) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        try {
            Dao<MonETSNotification, ?> dao = databaseHelper.getDao(MonETSNotification.class);
            for (MonETSNotification monETSNotification : notifications) {
                dao.createOrUpdate(monETSNotification);
            }

            listener.onRequestSuccess(notifications);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Async class used to retrieve a new token for MonÉTS if it's not valid anymore.
     *
     * The class itself is static to avoid leaks since it has a context attached to it. Though, a
     * {@link WeakReference} is used so the GC can collect it as needed.
     */
    private static class AsyncUpdateMonETSToken extends AsyncTask<Void, Void, String> {

        private WeakReference<Context> asyncContext;
        private NotificationManager notificationManager;

        private AsyncUpdateMonETSToken(Context context, NotificationManager manager) {
            asyncContext = new WeakReference<>(context);
            notificationManager = manager;
        }

        @Override
        public String doInBackground(Void... params) {
            Context context = asyncContext.get();

            if (context != null) {
                AccountManager accountManager = AccountManager.get(context);
                Account[] accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
                try {
                    return accountManager.blockingGetAuthToken(accounts[0], Constants.AUTH_TOKEN_TYPE, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return "";
        }

        @Override
        public void onPostExecute(String result) {
            if (!result.isEmpty()) {
                notificationManager.updateNotifications();
            }
        }
    }
}
