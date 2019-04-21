package ca.etsmtl.applets.etsmobile.service;

import android.app.PendingIntent;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import ca.etsmtl.applets.etsmobile.ui.activity.NotificationActivity;
import ca.etsmtl.applets.etsmobile.util.Constants;
import ca.etsmtl.applets.etsmobile.util.SecurePreferences;
import ca.etsmtl.applets.etsmobilenotifications.ETSFcmListenerService;
import ca.etsmtl.applets.etsmobilenotifications.model.MonETSNotification;

/**
 * Created by Sonphil on 20-04-19.
 */

public class AppETSFcmListenerService extends ETSFcmListenerService {
    Gson gson = new Gson();

    @Override
    protected List<MonETSNotification> savedNotifications() {
        SecurePreferences securePreferences = new SecurePreferences(getApplicationContext());
        String notificationsStr = securePreferences.getString(Constants.RECEIVED_NOTIF, "");

        return gson.fromJson(notificationsStr, new TypeToken<ArrayList<MonETSNotification>>() {}
                .getType());
    }

    @Override
    protected void saveNewNotification(MonETSNotification newNotification, List<MonETSNotification> previousNotifications) {
        List<MonETSNotification> notificationsToSave = new ArrayList<>(previousNotifications);
        notificationsToSave.add(newNotification);
        SecurePreferences securePreferences = new SecurePreferences(getApplicationContext());

        securePreferences.edit()
                .putString(Constants.RECEIVED_NOTIF, gson.toJson(notificationsToSave))
                .commit();
    }

    @Override
    protected PendingIntent notificationClickedIntent() {
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        return PendingIntent.getActivity(this, 0, intent, 0);
    }
}
