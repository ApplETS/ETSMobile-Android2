package ca.etsmtl.applets.etsmobile.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ca.etsmtl.applets.etsmobile.service.BottinService;

public class BottinReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, BottinService.class);
        context.startService(serviceIntent);
    }
}
