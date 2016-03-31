package ca.etsmtl.applets.etsmobile;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Locale;

import ca.etsmtl.applets.etsmobile.db.DatabaseHelper;
import ca.etsmtl.applets.etsmobile.model.Etudiant;
import ca.etsmtl.applets.etsmobile.model.MyMenuItem;
import ca.etsmtl.applets.etsmobile.model.UserCredentials;
import ca.etsmtl.applets.etsmobile.ui.activity.MainActivity;
import ca.etsmtl.applets.etsmobile.ui.fragment.AboutFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.BandwithFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.BiblioFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.BottinFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.CommentairesFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.FAQFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.HoraireFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.MoodleFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.NewsFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.NotesFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.OtherAppsFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.ProfilFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.SecuriteFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.SponsorsFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.TodayFragment;
import ca.etsmtl.applets.etsmobile.util.AnalyticsHelper;
import ca.etsmtl.applets.etsmobile.util.Constants;
import ca.etsmtl.applets.etsmobile.util.NoteManager;
import ca.etsmtl.applets.etsmobile.util.ProfilManager;
import ca.etsmtl.applets.etsmobile.util.SecurePreferences;
import ca.etsmtl.applets.etsmobile2.R;
import io.fabric.sdk.android.Fabric;
import io.supportkit.core.SupportKit;

public class ApplicationManager extends Application {

    public static UserCredentials userCredentials;
    public static String domaine;
    public static int typeUsagerId;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AnalyticsHelper.getInstance(this);
        createDatabaseTables();

//        SupportKit.init(this, getString(R.string.credentials_supportkit));
        Fabric.with(this, new Crashlytics());

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

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public static void deconnexion(final Activity activity) {


        final Editor editor = PreferenceManager.getDefaultSharedPreferences(activity).edit();
        editor.clear();
        editor.commit();

        // Enlever le profil de la DB SQLite
        new ProfilManager(activity).removeProfil();
        new NoteManager(activity).remove();

        AccountManager accountManager = AccountManager.get(activity);

        Account[] accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
        for (int index = 0; index < accounts.length; index++) {
            accountManager.removeAccount(accounts[index], null, null);
        }

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
