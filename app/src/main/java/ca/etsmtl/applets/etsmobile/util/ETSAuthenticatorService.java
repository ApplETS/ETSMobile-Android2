package ca.etsmtl.applets.etsmobile.util;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import ca.etsmtl.applets.etsmobile.ui.activity.LoginActivity;

/**
 * Created by gnut3ll4 on 15/10/15.
 */
public class ETSAuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {

        ETSMobileAuthenticator authenticator = new ETSMobileAuthenticator(this, LoginActivity.class);
        return authenticator.getIBinder();
    }
}