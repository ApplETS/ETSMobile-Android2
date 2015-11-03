package ca.etsmtl.applets.etsmobile.ui.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.IOException;
import java.util.Collection;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.model.MyMenuItem;
import ca.etsmtl.applets.etsmobile.service.RegistrationIntentService;
import ca.etsmtl.applets.etsmobile.ui.adapter.MenuAdapter;
import ca.etsmtl.applets.etsmobile.ui.fragment.AboutFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.TodayFragment;
import ca.etsmtl.applets.etsmobile.util.Constants;
import ca.etsmtl.applets.etsmobile.util.SecurePreferences;
import ca.etsmtl.applets.etsmobile2.R;
import io.supportkit.core.User;
import io.supportkit.ui.ConversationActivity;

/**
 * Main Activity for �TSMobile, handles the login and the menu
 *
 * @author Philippe David
 */
public class MainActivity extends Activity {

    private static final int REQUEST_CODE_EMAIL = 1;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mTitle;
    private ActionBarDrawerToggle mDrawerToggle;
    private Fragment fragment;
    private String TAG = "FRAGMENTTAG";
    private AccountManager accountManager;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private String registrationToken = "";
    private SecurePreferences securePreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        checkPlayServices();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        accountManager = AccountManager.get(this);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences.getBoolean(Constants.SENT_TOKEN_TO_SERVER, false);
            }
        };


        // Set the adapter for the list view
        int stringSet = ApplicationManager.mMenu.keySet().size();
        final Collection<MyMenuItem> myMenuItems = ApplicationManager.mMenu.values();

        MyMenuItem[] menuItems = new MyMenuItem[stringSet];
        mDrawerList.setAdapter(new MenuAdapter(this, myMenuItems.toArray(menuItems)));

        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
                mDrawerLayout, /* DrawerLayout object */
                R.drawable.ic_drawer, /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open, /* "open drawer" description */
                R.string.drawer_close /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(getString(R.string.drawer_title));
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);


        Account[] accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
        if (accounts.length > 0) {
            String authToken = accountManager.peekAuthToken(accounts[0], Constants.AUTH_TOKEN_TYPE);
            // validate the token, invalidate and generate a new one if required
            accountManager.invalidateAuthToken(Constants.ACCOUNT_TYPE, authToken);
            accountManager.getAuthToken(accounts[0], Constants.AUTH_TOKEN_TYPE, null, this, null, null);
        }

        securePreferences = new SecurePreferences(this);

        registrationToken = securePreferences.getString(Constants.GCM_REGISTRATION_TOKEN,"");
        if (TextUtils.isEmpty(registrationToken) && !TextUtils.isEmpty(ApplicationManager.domaine)) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();

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

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Constants.REGISTRATION_COMPLETE));

        if (ApplicationManager.userCredentials == null) {
            if (fragment == null) {
                selectItem(AboutFragment.class.getName());
            } else {
                MyMenuItem myMenuItem = ApplicationManager.mMenu.get(fragment.getTag());
                if (myMenuItem.hasToBeLoggedOn()) {
                    selectItem(AboutFragment.class.getName());
                }
                selectItem(fragment.getTag());
            }
        } else {
            if (fragment == null) {
                selectItem(TodayFragment.class.getName());
            }

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
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        FragmentManager manager = getFragmentManager();
        if (fragment != null) {
            manager.putFragment(outState, fragment.getTag(), fragment);
            outState.putString(TAG, fragment.getTag());
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        instantiateFragments(savedInstanceState);
    }

    private void instantiateFragments(Bundle savedInstanceState) {
        MyMenuItem ajdItem = ApplicationManager.mMenu.get(TodayFragment.class.getName());

        // Select Aujourd'Hui
        if (savedInstanceState != null) {
            FragmentManager fragmentManager = getFragmentManager();
            String tag = savedInstanceState.getString(TAG);
            fragment = fragmentManager.getFragment(savedInstanceState, tag);

        } else {
            selectItem(ajdItem.mClass.getName());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_EMAIL && resultCode == RESULT_OK) {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

            User user = User.getCurrentUser();
            user.setEmail(accountName);

            ConversationActivity.show(this);

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            ApplicationManager.deconnexion(this);
        }
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        else if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @SuppressWarnings("rawtypes")
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            MyMenuItem myMenuItem = (MyMenuItem) parent.getItemAtPosition(position);

            if (myMenuItem.resId == R.drawable.ic_ico_comment) {
                // Opens SupportKit with selected user account
                selectAccount();
            } else {
                selectItem(myMenuItem.mClass.getName());
            }
        }
    }

    /**
     * Swaps fragments in the main content view
     */
    @SuppressWarnings("rawtypes")
    private void selectItem(String key) {
        // Create a new fragment and specify the planet to show based on position
        fragment = null;
        MyMenuItem myMenuItem = ApplicationManager.mMenu.get(key);


        if (myMenuItem.hasToBeLoggedOn() && ApplicationManager.userCredentials == null) {

            final AccountManagerFuture<Bundle> future = accountManager.addAccount(Constants.ACCOUNT_TYPE, Constants.AUTH_TOKEN_TYPE, null, null, MainActivity.this, new AccountManagerCallback<Bundle>() {
                @Override
                public void run(AccountManagerFuture<Bundle> future) {
                    //Login successful
                }
            }, null);

        } else {
            Class aClass = myMenuItem.mClass;
            try {
                fragment = (Fragment) aClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            // Insert the fragment by replacing any existing fragment
            final FragmentManager fragmentManager = getFragmentManager();

            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, aClass.getName())
                    .addToBackStack(aClass.getName()).commit();

            // Update the title, and close the drawer
            setTitle(ApplicationManager.mMenu.get(key).title);
            mDrawerLayout.closeDrawer(mDrawerList);
        }

    }

    private void selectAccount() {
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_EMAIL);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
