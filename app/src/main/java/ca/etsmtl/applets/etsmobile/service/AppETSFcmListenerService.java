package ca.etsmtl.applets.etsmobile.service;

import android.app.PendingIntent;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import ca.etsmtl.applets.etsmobile.ui.activity.NotificationActivity;
import ca.etsmtl.applets.etsmobile.util.Constants;
import ca.etsmtl.applets.etsmobile.util.SecurePreferences;
import ca.etsmtl.applets.etsmobilenotifications.ETSFcmListenerService;
import ca.etsmtl.applets.etsmobilenotifications.EtsMobileNotificationManager;
import ca.etsmtl.applets.etsmobilenotifications.MonETSNotification;

/**
 * Created by Sonphil on 20-04-19.
 */

public class AppETSFcmListenerService extends ETSFcmListenerService {

    private Gson gson = new Gson();

    @Override
    protected EtsMobileNotificationManager getEtsMobileNotificationManager() {
        SecurePreferences securePreferences = new SecurePreferences(getApplicationContext());

        return new EtsMobileNotificationManager() {
            @Override
            public void saveNewNotification(MonETSNotification newNotification, List<MonETSNotification> previousNotifications) {
                List<MonETSNotification> notificationsToSave = new ArrayList<>(previousNotifications);
                notificationsToSave.add(newNotification);

                securePreferences.edit()
                        .putString(Constants.RECEIVED_NOTIF, gson.toJson(notificationsToSave))
                        .commit();
            }

            @Override
            public List<MonETSNotification> getNotifications() {
                String notificationsStr = securePreferences.getString(Constants.RECEIVED_NOTIF, "");

                List<MonETSNotification> notifications = gson.fromJson(notificationsStr,
                        new TypeToken<ArrayList<MonETSNotification>>() {}.getType());

                return notifications == null ? new ArrayList<>() : notifications;
            }
        };
    }

    @Nullable
    @Override
    protected PendingIntent notificationClickedIntent(MonETSNotification monETSNotification) {
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        return PendingIntent.getActivity(this, 0, intent, 0);
    }

    @Nullable
    @Override
    protected PendingIntent notificationDismissedIntent(MonETSNotification monETSNotification) {
        return super.notificationDismissedIntent(monETSNotification);
    }
}
