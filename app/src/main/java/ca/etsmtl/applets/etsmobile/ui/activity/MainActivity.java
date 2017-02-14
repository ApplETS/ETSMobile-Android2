package ca.etsmtl.applets.etsmobile.ui.activity;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.Locale;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.model.Etudiant;
import ca.etsmtl.applets.etsmobile.model.MyMenuItem;
import ca.etsmtl.applets.etsmobile.service.RegistrationIntentService;
import ca.etsmtl.applets.etsmobile.ui.fragment.AboutFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.BandwithFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.BaseFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.BiblioFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.BottinFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.CommentairesFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.EventsFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.FAQFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.HoraireFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.MonETSFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.MoodleFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.NewsFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.NotesFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.OtherAppsFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.ProfilFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.SecuriteFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.SponsorsFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.TodayFragment;
import ca.etsmtl.applets.etsmobile.util.Constants;
import ca.etsmtl.applets.etsmobile.util.ProfilManager;
import ca.etsmtl.applets.etsmobile.util.SecurePreferences;
import ca.etsmtl.applets.etsmobile2.R;
import io.supportkit.core.User;
import io.supportkit.ui.ConversationActivity;

/**
 * Main Activity for �TSMobile, handles the login and the Navigation Drawer (menu)
 *
 * @author Philippe David
 */
public class MainActivity extends AppCompatActivity {

    public static final int SCHEDULE_FRAGMENT = 1;
    public static final int COURSE_FRAGMENT = 2;
    public static final int MOODLE_FRAGMENT = 3;
    public static final int PROFILE_FRAGMENT = 4;
    public static final int MONETS_FRAGMENT = 5;
    public static final int BANDWIDTH_FRAGMENT = 7;
    public static final int NEWS_FRAGMENT = 8;
    public static final int DIRECTORY_FRAGMENT = 9;
    public static final int LIBRARY_FRAGMENT = 10;
    public static final int SECURITY_FRAGMENT = 11;
    public static final int ACHIEVEMENTS_FRAGMENT = 12;
    public static final int ABOUT_FRAGMENT = 13;
    public static final int COMMENTS_FRAGMENT = 14;
    public static final int FAQ_FRAGMENT = 15;
    public static final int SPONSOR_FRAGMENT = 16;
    public static final int TODAY_FRAGMENT = 17;

    public static final long LOGIN = 18;
    public static final long LOGOUT = 19;


    private String TAG = "MainActivity";
    private AccountManager accountManager;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isGCMTokenSent;
    private SecurePreferences securePreferences;
    public SharedPreferences prefs;

    private AccountHeader headerResult = null;
    private Drawer result = null;


    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            goToFragment(new TodayFragment(), TodayFragment.getClassName());
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        checkPlayServices();
        setLocale();
        initDrawer();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        accountManager = AccountManager.get(this);
//

        isGCMTokenSent = sharedPreferences.getBoolean(Constants.IS_GCM_TOKEN_SENT_TO_SERVER, false);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                isGCMTokenSent = sharedPreferences.getBoolean(Constants.IS_GCM_TOKEN_SENT_TO_SERVER, false);
            }
        };

        securePreferences = new SecurePreferences(this);

    }

    private void initDrawer() {
        boolean isUserLoggedIn = ApplicationManager.userCredentials != null;
        String studentName = "";
        String codeUniversel = "";
        ProfilManager profilManager = new ProfilManager(this);
        Etudiant etudiant = profilManager.getEtudiant();
        if(etudiant != null){

            studentName = profilManager.getEtudiant().prenom.replace("  ","") +" "+ profilManager.getEtudiant().nom.replace(" ","");
            codeUniversel= profilManager.getEtudiant().codePerm;
        }
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.ets_background_grayscale)
                .addProfiles(
                        new ProfileDrawerItem().withName(codeUniversel).withEmail(studentName).withSelectedTextColor(ContextCompat.getColor(this,R.color.red)).withIcon(R.drawable.ic_user)
                ).withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        goToFragment(new ProfilFragment(), ProfilFragment.getClassName());
                        return false;
                    }
                }).build();

        DrawerBuilder drawerBuilder = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .withSelectedItem(TODAY_FRAGMENT)
                .withDisplayBelowStatusBar(true)
                //                .withActionBarDrawerToggle(true)
                .addDrawerItems(
                        new ExpandableDrawerItem().withName(R.string.menu_section_1_moi).withSelectable(false).withSubItems(
                                new SecondaryDrawerItem().withName(R.string.menu_section_1_ajd).withIdentifier(TODAY_FRAGMENT).withIcon(R.drawable.ic_ico_aujourdhui).withEnabled(isUserLoggedIn),
                                new SecondaryDrawerItem().withName(R.string.menu_section_1_horaire).withIdentifier(SCHEDULE_FRAGMENT).withIcon(R.drawable.ic_ico_schedule).withEnabled(isUserLoggedIn),
                                new SecondaryDrawerItem().withName(R.string.menu_section_1_notes).withIdentifier(COURSE_FRAGMENT).withIcon(R.drawable.ic_ico_notes).withEnabled(isUserLoggedIn),
                                new SecondaryDrawerItem().withName(R.string.menu_section_2_moodle).withIdentifier(MOODLE_FRAGMENT).withIcon(R.drawable.ic_moodle_icon_small).withEnabled(isUserLoggedIn),
                                new SecondaryDrawerItem().withName(R.string.menu_section_1_monETS).withIdentifier(MONETS_FRAGMENT).withIcon(R.drawable.ic_monets).withEnabled(isUserLoggedIn),
                                new SecondaryDrawerItem().withName(R.string.menu_section_1_bandwith).withIdentifier(BANDWIDTH_FRAGMENT).withIcon(R.drawable.ic_ico_internet)
                        ).withIsExpanded(true),
                        new ExpandableDrawerItem().withName(R.string.menu_section_2_ets).withSelectable(false).withSubItems(
                                new SecondaryDrawerItem().withName(R.string.menu_section_2_news).withIdentifier(NEWS_FRAGMENT).withIcon(R.drawable.ic_ico_news),
                                new SecondaryDrawerItem().withName(R.string.menu_section_2_bottin).withIdentifier(DIRECTORY_FRAGMENT).withIcon(R.drawable.ic_ico_bottin).withSelectable(false),
                                new SecondaryDrawerItem().withName(R.string.menu_section_2_biblio).withIdentifier(LIBRARY_FRAGMENT).withIcon(R.drawable.ic_ico_library),
                                new SecondaryDrawerItem().withName(R.string.menu_section_2_securite).withIdentifier(SECURITY_FRAGMENT).withIcon(R.drawable.ic_ico_security)
                        ),
                        new ExpandableDrawerItem().withName(R.string.menu_section_3_applets).withSelectable(false).withSubItems(
                                new SecondaryDrawerItem().withName(R.string.menu_section_3_apps).withIdentifier(ACHIEVEMENTS_FRAGMENT).withIcon(R.drawable.ic_star_60x60),
                                new SecondaryDrawerItem().withName(R.string.menu_section_3_about).withIdentifier(ABOUT_FRAGMENT).withIcon(R.drawable.ic_logo_icon_final),
                                new SecondaryDrawerItem().withName(R.string.menu_section_3_comms).withIdentifier(COMMENTS_FRAGMENT).withIcon(R.drawable.ic_ico_comment),
                                new SecondaryDrawerItem().withName(R.string.menu_section_3_sponsors).withIdentifier(SPONSOR_FRAGMENT).withIcon(R.drawable.ic_ico_partners),
                                new SecondaryDrawerItem().withName(R.string.menu_section_3_faq).withIdentifier(FAQ_FRAGMENT).withIcon(R.drawable.ic_ico_faq)
                        )


                );
        if (isUserLoggedIn)
            drawerBuilder.addStickyDrawerItems(new SecondaryDrawerItem().withName(R.string.action_logout).withIdentifier(LOGOUT).withTextColorRes(R.color.red));
        else
            drawerBuilder.addStickyDrawerItems(new SecondaryDrawerItem().withName(R.string.action_login).withIdentifier(LOGIN));

        drawerBuilder.withOnDrawerItemClickListener(drawerItemClickListener);


        result = drawerBuilder.build();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    protected void onStart() {
        super.onStart();
        DataManager.getInstance(this).start();
    }

    @Override
    protected void onStop() {
        DataManager.getInstance(this).stop();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //In case of : retry registering to GCM
        if (!isGCMTokenSent && ApplicationManager.domaine != null) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Constants.REGISTRATION_COMPLETE));

        if (ApplicationManager.userCredentials == null) {
            goToFragment(new AboutFragment(), AboutFragment.class.getName());
        }

    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        Constants.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    public void deconnexion() {
        ApplicationManager.deconnexion(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//
        if (requestCode == Constants.REQUEST_CODE_EMAIL && resultCode == RESULT_OK) {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

            User user = User.getCurrentUser();
            user.setEmail(accountName);

            ConversationActivity.show(this);

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_language) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(getResources().getString(R.string.lang_title));
            builder.setMessage(getResources().getString(R.string.lang_description));
            builder.setPositiveButton(getResources().getString(R.string.lang_choix_positif),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            prefs.edit().remove("language").apply();
                            prefs.edit().putString("language", "fr").apply();
//                    mMenu = new LinkedHashMap<String, MyMenuItem>(17);
                            recreate();
                        }
                    });
            builder.setNegativeButton(getResources().getString(R.string.lang_choix_negatif),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            prefs.edit().remove("language").apply();
                            prefs.edit().putString("language", "en").apply();
//                    mMenu = new LinkedHashMap<String, MyMenuItem>(17);
                            recreate();
                        }
                    });
            builder.show();
        }

        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
//        else if (mDrawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    private void setLocale() {
        Locale locale;
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        prefs = this.getSharedPreferences("Language", 0);
        String restoredText = prefs.getString("language", "");
        if (restoredText.equalsIgnoreCase("en")) {
            locale = Locale.ENGLISH;
        } else if (restoredText.equalsIgnoreCase("fr"))
            locale = Locale.CANADA_FRENCH;
        else
            locale = Locale.getDefault();
        Locale.setDefault(locale);
        conf = new Configuration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);

    }


    /**
     * Asks the user to pick a Google account so that we can have his email in slack support with
     * SupportKit
     */
    private void selectAccount() {
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                new String[]{"com.google"}, false, null, null, null, null);//TODO get the actual GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE ASAP!!!


        startActivityForResult(intent, Constants.REQUEST_CODE_EMAIL);
    }

    @Override
    public void setTitle(CharSequence title) {
//        mTitle = title;
//        getActionBar().setTitle(mTitle);
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    private Drawer.OnDrawerItemClickListener drawerItemClickListener = new Drawer.OnDrawerItemClickListener() {
        @Override
        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();

            if (drawerItem != null) {

                if (drawerItem.getIdentifier() == SCHEDULE_FRAGMENT) {
                    goToFragment(new HoraireFragment(), HoraireFragment.getClassName());
                } else if (drawerItem.getIdentifier() == TODAY_FRAGMENT) {
                    goToFragment(new TodayFragment(), TodayFragment.getClassName());
                } else if (drawerItem.getIdentifier() == MOODLE_FRAGMENT) {
                    goToFragment(new MoodleFragment(), MoodleFragment.getClassName());
                } else if (drawerItem.getIdentifier() == COURSE_FRAGMENT) {
                    goToFragment(new NotesFragment(), NotesFragment.getClassName());
                } else if (drawerItem.getIdentifier() == PROFILE_FRAGMENT) {
                    goToFragment(new ProfilFragment(), ProfilFragment.getClassName());
                } else if (drawerItem.getIdentifier() == MONETS_FRAGMENT) {
                    goToFragment(new MonETSFragment(), MonETSFragment.getClassName());
                } else if (drawerItem.getIdentifier() == BANDWIDTH_FRAGMENT) {
                    goToFragment(new BandwithFragment(), BandwithFragment.getClassName());
                } else if (drawerItem.getIdentifier() == NEWS_FRAGMENT) {
                    goToFragment(new NewsFragment(), NewsFragment.getClassName());
                } else if (drawerItem.getIdentifier() == LIBRARY_FRAGMENT) {
                    goToFragment(new BiblioFragment(), BiblioFragment.getClassName());
                } else if (drawerItem.getIdentifier() == DIRECTORY_FRAGMENT) {
                    startActivity(new Intent(getApplicationContext(),BotinActivity.class));
                } else if (drawerItem.getIdentifier() == SECURITY_FRAGMENT) {
                    goToFragment(new SecuriteFragment(), SecuriteFragment.getClassName());
                } else if (drawerItem.getIdentifier() == FAQ_FRAGMENT) {
                    goToFragment(new FAQFragment(), FAQFragment.getClassName());
                } else if (drawerItem.getIdentifier() == ACHIEVEMENTS_FRAGMENT) {
                    goToFragment(new OtherAppsFragment(), OtherAppsFragment.getClassName());
                } else if (drawerItem.getIdentifier() == ABOUT_FRAGMENT) {
                    goToFragment(new AboutFragment(), AboutFragment.getClassName());
                } else if (drawerItem.getIdentifier() == SPONSOR_FRAGMENT) {
                    goToFragment(new SponsorsFragment(), SponsorsFragment.getClassName());
                } else if (drawerItem.getIdentifier() == COMMENTS_FRAGMENT) {
                    selectAccount();
                } else if (drawerItem.getIdentifier() == LOGIN) {
                    final AccountManagerFuture<Bundle> future = accountManager.addAccount(Constants.ACCOUNT_TYPE, Constants.AUTH_TOKEN_TYPE, null, null, MainActivity.this, new AccountManagerCallback<Bundle>() {
                        @Override
                        public void run(AccountManagerFuture<Bundle> future) {
                            //Login successful
                        }
                    }, null);
                } else if (drawerItem.getIdentifier() == LOGOUT) {
                    openLogoutDialogAlert();
                }

            }
            return false;
        }
    };
    public void openLogoutDialogAlert(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title
        alertDialogBuilder.setTitle(getString(R.string.action_logout));

        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.logout_confirmation)
                .setPositiveButton(android.R.string.yes,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        deconnexion();
                        dialog.dismiss();

                    }
                })
                .setNegativeButton(android.R.string.cancel,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {

                        dialog.dismiss();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
    public void goToFragment(Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(tag)
                .commit();

//        setTitle(((BaseFragment)fragment).getFragmentTitle());
        this.invalidateOptionsMenu();
    }

    public void setTitle(String title){
        getSupportActionBar().setTitle(title);
    }

}
