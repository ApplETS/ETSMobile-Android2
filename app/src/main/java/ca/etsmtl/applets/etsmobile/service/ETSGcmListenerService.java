package ca.etsmtl.applets.etsmobile.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import java.util.LinkedList;
import java.util.Random;

import ca.etsmtl.applets.etsmobile.ui.activity.MainActivity;
import ca.etsmtl.applets.etsmobile.util.Constants;
import ca.etsmtl.applets.etsmobile.util.SecurePreferences;
import ca.etsmtl.applets.etsmobile2.R;
import io.supportkit.core.GcmService;

/**
 * Created by gnut3ll4 on 16/10/15.
 */
public class ETSGcmListenerService extends GcmService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {

        // Calls SupportKit GCM Listener
        if (TextUtils.equals(data.getString("origin"), "SupportKit")) {
            super.onMessageReceived(from, data);
        } else {


            /**
             * Production applications would usually process the message here.
             * Eg: - Syncing with server.
             *     - Store message in local database.
             *     - Update UI.
             */

            /**
             * In some cases it may be useful to show a notification indicating to the user
             * that a message was received.
             */
            sendNotification(data);
        }
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param data GCM message received.
     */
    private void sendNotification(Bundle data) {

        String notificationTexte = data.getString("NotificationTexte");
        String notificationApplicationNom = data.getString("NotificationApplicationNom");
        String notificationSigleCours = data.getString("NotificationSigleCours");
        String url = data.getString("Url");
        String notificationDateDebutAffichage = data.getString("NotificationDateDebutAffichage");

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));

        SecurePreferences securePreferences = new SecurePreferences(this);
        int id = securePreferences.getInt(Constants.NOTIFICATION_ID, 1);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_ets);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.school_48)
                .setLargeIcon(bm)
                .setContentTitle(notificationApplicationNom)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationTexte))
                .setContentText(notificationTexte)
                .setAutoCancel(true)
                .setGroup(Constants.GROUP_KEY_NOTIFICATIONS)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        notificationManager.notify(id, notificationBuilder.build());
        id++;
        securePreferences.edit().putInt(Constants.NOTIFICATION_ID, id).commit();
    }

}