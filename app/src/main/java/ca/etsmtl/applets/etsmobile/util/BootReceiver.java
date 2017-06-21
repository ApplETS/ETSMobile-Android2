package ca.etsmtl.applets.etsmobile.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Récepteur recevant un intent au démarrage de l'appareil
 * <p>
 * Created by Sonphil on 21-06-17.
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        BottinReceiver.scheduleAlarmBottinService(context);
    }
}
