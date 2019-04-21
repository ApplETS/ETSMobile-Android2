package ca.etsmtl.applets.etsmobile;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.firebase.FirebaseApp;

import java.sql.SQLException;

import androidx.multidex.MultiDex;
import ca.etsmtl.applets.etsmobile.db.DatabaseHelper;
import ca.etsmtl.applets.etsmobile.di.AppComponent;
import ca.etsmtl.applets.etsmobile.di.AppModule;
import ca.etsmtl.applets.etsmobile.di.DaggerAppComponent;
import ca.etsmtl.applets.etsmobile.model.Etudiant;
import ca.etsmtl.applets.etsmobile.model.UserCredentials;
import ca.etsmtl.applets.etsmobile.ui.activity.MainActivity;
import ca.etsmtl.applets.etsmobile.util.Constants;
import ca.etsmtl.applets.etsmobile.util.NoteManager;
import ca.etsmtl.applets.etsmobile.util.ProfilManager;
import ca.etsmtl.applets.etsmobile.util.SecurePreferences;
import ca.etsmtl.applets.etsmobilenotifications.NotificationsLoginManager;

public class ApplicationManager extends Application {

    public static UserCredentials userCredentials;
    public static String domaine;
    public static int typeUsagerId;
    private static AppComponent appComponent;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createDatabaseTables();

//        Fabric.with(this, new Crashlytics());
        FirebaseApp.initializeApp(this);

        AccountManager accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
        String username = "", password = "";

        if (accounts.length > 0) {
            username = accounts[0].name;
            password = accountManager.getPassword(accounts[0]);
        }

        if (username.length() > 0 && password.length() > 0) {
            userCredentials = new UserCredentials(username, password);
        }

        SecurePreferences securePreferences = new SecurePreferences(this);
        int typeUsagerId = securePreferences.getInt(Constants.TYPE_USAGER_ID, -1);
        String domaine = securePreferences.getString(Constants.DOMAINE, "");

        if(typeUsagerId != -1 && !TextUtils.isEmpty(domaine)) {
            ApplicationManager.typeUsagerId = typeUsagerId;
            ApplicationManager.domaine = domaine;
        }

        appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
    }

    public static AppComponent getAppComponent() {
        return appComponent;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public static void deconnexion(final Activity activity) {

        Editor editor = PreferenceManager.getDefaultSharedPreferences(activity).edit();
        Editor secureEditor = new SecurePreferences(activity).edit();
        editor.clear();
        secureEditor.clear();

        // Enlever le profil de la DB SQLite
        new ProfilManager(activity).removeProfil();
        new NoteManager(activity).remove();

        AccountManager accountManager = AccountManager.get(activity);

        Account[] accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
        for (int index = 0; index < accounts.length; index++) {
            accountManager.removeAccount(accounts[index], null, null);
        }

        NotificationsLoginManager.logout(activity);

        editor.apply();
        secureEditor.apply();

        ApplicationManager.userCredentials = null;
        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        new Thread(new Runnable() {

            @Override
            public void run() {
                activity.finish();
            }
        }).start();

    }

    /**
     * Creates database tables in advance to avoid heavy processing during login
     */
    private void createDatabaseTables() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        try {
            databaseHelper.getDao(Etudiant.class).queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
