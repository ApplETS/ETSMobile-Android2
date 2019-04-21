package ca.etsmtl.applets.etsmobilenotifications;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

/**
 * Created by gnut3ll4 on 16/10/15.
 */
public abstract class ETSFcmListenerService extends FirebaseMessagingService {

    private static final String TAG = "MyFcmListenerService";

    /**
     * Called when message is received.
     *
     * @param message containing the data from the sender and from the message itself
     *                (Bundle containing message data as key/value pairs.
     */
    @Override
    public void onMessageReceived(RemoteMessage message) {

        Map<String, String> data = message.getData();

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

    @Override
    public void onNewToken(String token) {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        super.onNewToken(token);
        FcmRegistrationIntentService.enqueueWork(this, new Intent());
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param data FCM message received.
     */
    private void sendNotification(Map<String, String> data) {
        List<MonETSNotification> previousMonETSNotifications = savedNotifications();
        ArrayList<MonETSNotification> monETSNotifications = new ArrayList<>(previousMonETSNotifications);
        MonETSNotification nouvelleMonETSNotification = getMonETSNotificationFromMap(data);

        if (nouvelleMonETSNotification != null) {
            monETSNotifications.add(nouvelleMonETSNotification);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannel();
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        Notification summaryNotification = createSummaryNotification(monETSNotifications);

        notifyNotifications(notificationManager, monETSNotifications);
        notificationManager.notify(Constants.NOTIFICATIONS_SUMMARY_ID, summaryNotification);

        saveNewNotification(nouvelleMonETSNotification, previousMonETSNotifications);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannel() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            NotificationChannel channel = notificationManager.getNotificationChannel(Constants.DEFAULT_NOTIFICATION_CHANNEL_ID);

            if (channel == null) {
                // We could create multiple channels based on the notification but let's just create one for maintenance purposes.
                String channelName = getString(R.string.fcm_fallback_notification_channel_label);
                channel = new NotificationChannel(Constants.DEFAULT_NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void notifyNotifications(NotificationManagerCompat notificationManager,
                                     List<MonETSNotification> monETSNotifications) {
        for (MonETSNotification monETSNotification : monETSNotifications) {
            Notification notification = new NotificationCompat.Builder(this, Constants.DEFAULT_NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_school_white_24dp)
                    .setColor(ContextCompat.getColor(this, R.color.ets_red))
                    .setContentTitle(monETSNotification.getNotificationApplicationNom())
                    .setContentText(monETSNotification.getNotificationTexte())
                    .setContentIntent(notificationClickedIntent())
                    .setAutoCancel(true)
                    .setGroup(Constants.NOTIFICATIONS_GROUP_KEY)
                    .build();

            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            notificationManager.notify(monETSNotification.getId(), notification);
        }
    }

    private Notification createSummaryNotification(List<MonETSNotification> notifications) {
        int numberOfNotifications = notifications.size();

        String bigContentTitle = getString(R.string.notification_content_title,
                numberOfNotifications + "",
                (numberOfNotifications == 1 ? "" : "s"),
                (numberOfNotifications == 1 ? "" : "s"));


        return new NotificationCompat.Builder(this, Constants.DEFAULT_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_school_white_24dp)
                .setColor(ContextCompat.getColor(this, R.color.ets_red))
                .setContentTitle(getString(R.string.ets))
                .setContentText(bigContentTitle)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setStyle(summaryNotificationStyle(notifications))
                .setGroup(Constants.NOTIFICATIONS_GROUP_KEY)
                .setGroupSummary(true)
                .build();
    }

    private NotificationCompat.Style summaryNotificationStyle(List<MonETSNotification> notifications) {
        NotificationCompat.InboxStyle inBoxStyle = new NotificationCompat.InboxStyle();

        for (MonETSNotification monETSNotification : notifications) {
            inBoxStyle.addLine(monETSNotification.getNotificationTexte());
        }

        return inBoxStyle;
    }

    private MonETSNotification getMonETSNotificationFromMap(Map<String, String> data) {
        String idStr = data.get("Id");

        if (idStr == null) {
            return null;
        }

        int id = Integer.valueOf(idStr);
        String notificationTexte = data.get("NotificationTexte");

        String notificationApplicationNom = data.get("NotificationApplicationNom");
        String url = data.get("Url");

        return new MonETSNotification(
                id,
                0,
                notificationTexte,
                null,
                notificationApplicationNom,
                url);
    }

    /**
     * Returns the maximum number of notifications displayed at the same time
     *
     * @return The maximum number of notifications displayed at the same time
     */
    protected int maxNbNotificationsToDisplay() {
        return 5;
    }

    /**
     * Get the previous notifications persisted on the device
     *
     * @return The previous notifications persisted on the device
     */
    protected abstract List<MonETSNotification> savedNotifications();

    /**
     * Save new notification on device
     *
     * @param newNotification New notification to save
     * @param previousNotifications Previous notifications
     */
    protected abstract void saveNewNotification(MonETSNotification newNotification, List<MonETSNotification> previousNotifications);

    /**
     * Get {@link PendingIntent} for the {@link Activity} that should be launched when the user tap
     * on a notification
     *
     * @see <a href="https://developer.android.com/training/notify-user/navigation">
     *     https://developer.android.com/training/notify-user/navigation
     *     </a>
     *
     * @return {@link PendingIntent} for the {@link Activity} that should be launched when the user
     * tap on a notification
     */
    protected abstract PendingIntent notificationClickedIntent();
}