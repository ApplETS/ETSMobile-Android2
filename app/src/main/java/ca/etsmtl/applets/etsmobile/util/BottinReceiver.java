package ca.etsmtl.applets.etsmobile.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ca.etsmtl.applets.etsmobile.service.BottinService;

/**
 * Récepteur démarrant le service du bottin
 * <p>
 * Created by Sonphil on 20-06-17.
 */

public class BottinReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, BottinService.class);
        context.startService(serviceIntent);
    }

    /**
     * Programmation d'une alarme qui déclenche périodiquement {@link BottinReceiver}
     *
     * @param context
     */
    public static void scheduleAlarmBottinService(Context context) {
        Intent intent = new Intent(context, BottinReceiver.class);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                Constants.REQUEST_CODE_SYNC_BOTTIN_BROADCAST, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
