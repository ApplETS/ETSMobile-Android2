package ca.etsmtl.applets.etsmobile;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;

import java.sql.SQLException;
import java.util.LinkedHashMap;

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
import ca.etsmtl.applets.etsmobile.ui.fragment.RadioFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.SecuriteFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.SponsorsFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.TodayFragment;
import ca.etsmtl.applets.etsmobile.util.Constants;
import ca.etsmtl.applets.etsmobile.util.NoteManager;
import ca.etsmtl.applets.etsmobile.util.ProfilManager;
import ca.etsmtl.applets.etsmobile.util.SecurePreferences;
import ca.etsmtl.applets.etsmobile2.R;
import io.fabric.sdk.android.Fabric;
import io.supportkit.core.SupportKit;

/**
 * Created by Phil on 17/11/13.
 */
public class ApplicationManager extends Application {


    public static LinkedHashMap<String, MyMenuItem> mMenu = new LinkedHashMap<String, MyMenuItem>(17);
    public static UserCredentials userCredentials;
    public static String domaine;
    public static int typeUsagerId;

    @Override
    public void onCreate() {
        super.onCreate();

        createDatabaseTables();

        SupportKit.init(this, getString(R.string.credentials_supportkit));
        Fabric.with(this, new Crashlytics());

        // Section 1 - Moi
        mMenu.put(getString(R.string.menu_section_1_moi),
                new MyMenuItem(
                        getString(R.string.menu_section_1_moi),
                        null
                ));

        mMenu.put(TodayFragment.class.getName(),
                new MyMenuItem(
                        getString(R.string.menu_section_1_ajd),
                        TodayFragment.class,
                        R.drawable.ic_ico_aujourdhui,
                        true
                ));

        mMenu.put(HoraireFragment.class.getName(),
                new MyMenuItem(
                        getString(R.string.menu_section_1_horaire),
                        HoraireFragment.class,
                        R.drawable.ic_ico_schedule,
                        true
                ));

        mMenu.put(NotesFragment.class.getName(),
                new MyMenuItem(
                        getString(R.string.menu_section_1_notes),
                        NotesFragment.class,
                        R.drawable.ic_ico_notes,
                        true
                ));

        mMenu.put(MoodleFragment.class.getName(),
                new MyMenuItem(
                        getString(R.string.menu_section_2_moodle),
                        MoodleFragment.class,
                        R.drawable.ic_moodle_icon_small,
                        true
                ));

        mMenu.put(ProfilFragment.class.getName(),
                new MyMenuItem(
                        getString(R.string.menu_section_1_profil),
                        ProfilFragment.class,
                        R.drawable.ic_ico_profil,
                        true
                ));

        mMenu.put(BandwithFragment.class.getName(),
                new MyMenuItem(
                        getString(R.string.menu_section_1_bandwith),
                        BandwithFragment.class,
                        R.drawable.ic_ico_internet,
                        false
                ));


        // Section 2 - ÉTS
        mMenu.put(getString(R.string.menu_section_2_ets),
                new MyMenuItem(
                        getString(R.string.menu_section_2_ets),
                        null
                ));

        mMenu.put(NewsFragment.class.getName(),
                new MyMenuItem(
                        getString(R.string.menu_section_2_news),
                        NewsFragment.class,
                        R.drawable.ic_ico_news,
                        false
                ));


        mMenu.put(BottinFragment.class.getName(),
                new MyMenuItem(
                        getString(R.string.menu_section_2_bottin),
                        BottinFragment.class,
                        R.drawable.ic_ico_bottin,
                        false
                ));

        mMenu.put(BiblioFragment.class.getName(),
                new MyMenuItem(
                        getString(R.string.menu_section_2_biblio),
                        BiblioFragment.class,
                        R.drawable.ic_ico_library,
                        false
                ));

        mMenu.put(RadioFragment.class.getName(),
                new MyMenuItem(
                        getString(R.string.menu_section_2_radio),
                        RadioFragment.class,
                        R.drawable.ic_ico_radio,
                        false
                ));

        mMenu.put(SecuriteFragment.class.getName(),
                new MyMenuItem(
                        getString(R.string.menu_section_2_securite),
                        SecuriteFragment.class,
                        R.drawable.ic_ico_security,
                        false
                ));

        // Section 3 - ApplETS
        mMenu.put(getString(R.string.menu_section_3_applets),
                new MyMenuItem(
                        getString(R.string.menu_section_3_applets),
                        null
                ));

        mMenu.put(OtherAppsFragment.class.getName(),
                new MyMenuItem(
                        getString(R.string.menu_section_3_apps),
                        OtherAppsFragment.class,
                        R.drawable.ic_star_60x60,
                        false
                ));

        mMenu.put(AboutFragment.class.getName(),
                new MyMenuItem(
                        getString(R.string.menu_section_3_about),
                        AboutFragment.class,
                        R.drawable.ic_logo_icon_final,
                        false
                ));

        mMenu.put(CommentairesFragment.class.getName(),
                new MyMenuItem(
                        getString(R.string.menu_section_3_comms),
                        CommentairesFragment.class,
                        R.drawable.ic_ico_comment,
                        false
                ));

        mMenu.put(SponsorsFragment.class.getName(),
                new MyMenuItem(
                        getString(R.string.menu_section_3_sponsors),
                        SponsorsFragment.class,
                        R.drawable.ic_ico_partners,
                        false
                ));

        mMenu.put(FAQFragment.class.getName(),
                new MyMenuItem(
                        getString(R.string.menu_section_3_faq),
                        FAQFragment.class,
                        R.drawable.ic_ico_faq,
                        false
                ));


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
