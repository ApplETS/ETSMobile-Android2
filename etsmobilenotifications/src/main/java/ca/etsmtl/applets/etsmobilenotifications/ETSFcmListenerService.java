package ca.etsmtl.applets.etsmobilenotifications;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import ca.etsmtl.applets.etsmobilenotifications.model.MonETSNotification;

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
        List<MonETSNotification> previousNotifications = savedNotifications();
        ArrayList<MonETSNotification> notifications = new ArrayList<>(previousNotifications);
        MonETSNotification nouvelleNotification = getMonETSNotificationFromMap(data);

        if (nouvelleNotification != null) {
            notifications.add(nouvelleNotification);
        }

        int numberOfNotifications = notifications.size();

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = mNotificationManager.getNotificationChannel(Constants.DEFAULT_NOTIFICATION_CHANNEL_ID);
            if (channel == null) {
                // We could create multiple channels based on the notification but let's just create one for maintenance purposes.
                String channelName = getString(R.string.fcm_fallback_notification_channel_label);
                channel = new NotificationChannel(Constants.DEFAULT_NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
                mNotificationManager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, Constants.DEFAULT_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_school_white_24dp)
                .setColor(ContextCompat.getColor(this, R.color.ets_red))
                .setContentTitle(getString(R.string.ets))
                .setContentText(getString(R.string.new_notifications))
                .setContentIntent(notificationClickedIntent())
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setAutoCancel(true)
                .setNumber(numberOfNotifications);

        NotificationCompat.InboxStyle inBoxStyle = new NotificationCompat.InboxStyle();

        // Sets a title for the Inbox in expanded layout
        String bigContentTitle = getString(R.string.notification_content_title,
                numberOfNotifications + "",
                (numberOfNotifications == 1 ? "" : "s"),
                (numberOfNotifications == 1 ? "" : "s"));
        inBoxStyle.setBigContentTitle(bigContentTitle);

        String username = NotificationsLoginManager.getUserName(getApplicationContext());
        Spannable sb = new SpannableString(username);
        sb.setSpan(
                new StyleSpan(android.graphics.Typeface.BOLD),
                0,
                username.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        inBoxStyle.setSummaryText(sb);

        saveNewNotification(nouvelleNotification, previousNotifications);

        int nbNotificationsToDisplay = maxNbNotificationsToDisplay();
        int minimumIndex = notifications.size() - nbNotificationsToDisplay;
        minimumIndex = minimumIndex < 0 ? 0 : minimumIndex;
        for (int i = notifications.size() - 1; i >= minimumIndex; i--) {
            inBoxStyle.addLine(notifications.get(i).getNotificationTexte());
        }

        if (numberOfNotifications > nbNotificationsToDisplay) {

            int plusOthers = (numberOfNotifications - nbNotificationsToDisplay);
            String plusOthersString = getString(R.string.others_notifications, plusOthers + "", (plusOthers == 1 ? "" : "s"));
            Spannable others = new SpannableString(plusOthersString);
            others.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, others.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            inBoxStyle.addLine(others);
        }

        mBuilder.setStyle(inBoxStyle);
        mNotificationManager.notify(1, mBuilder.build());
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