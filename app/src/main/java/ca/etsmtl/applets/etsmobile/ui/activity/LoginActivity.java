package ca.etsmtl.applets.etsmobile.ui.activity;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.URL;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.model.Etudiant;
import ca.etsmtl.applets.etsmobile.model.UserCredentials;
import ca.etsmtl.applets.etsmobile.util.Constants;
import ca.etsmtl.applets.etsmobile.util.SecurePreferences;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends AccountAuthenticatorActivity implements RequestListener<Object> {

    // Values for email and password at the time of the login attempt.
    private String mEmail;
    private String mPassword;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;

    private DataManager dataManager;
    private UserCredentials userCredentials;

    private AccountManager accountManager;
    public AccountAuthenticatorResponse response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataManager = DataManager.getInstance(getApplicationContext());

        setContentView(R.layout.activity_login);

        accountManager = AccountManager.get(getBaseContext());

        // Set up the login form.
        // mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
        mEmailView = (EditText) findViewById(R.id.email);
        mEmailView.setText(mEmail);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mLoginStatusView = findViewById(R.id.login_status);
        mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mEmail = mEmailView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mEmail)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!mEmail.matches("[a-zA-z]{2}(\\d){5}")) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
            showProgress(true);
            userCredentials = new UserCredentials(mEmail, mPassword);

            dataManager.login(userCredentials, this);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which
                // allow for very easy animations. If available, use these APIs to
                // fade-in the progress spinner.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                    int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

                    mLoginStatusView.setVisibility(View.VISIBLE);
                    mLoginStatusView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                                }
                            });

                    mLoginFormView.setVisibility(View.VISIBLE);
                    mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                                }
                            });
                } else {
                    // The ViewPropertyAnimator APIs are not available, so simply show
                    // and hide the relevant UI components.
                    mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }

            }
        });
    }

    @Override
    public void onRequestFailure(SpiceException e) {
        showProgress(false);
        mPasswordView.setError(getString(R.string.error_invalid_email));
        mPasswordView.requestFocus();
    }

    @Override
    public void onRequestSuccess(Object o) {
        showProgress(false);

        if (o != null) {
            Etudiant etudiant = (Etudiant) o;
            if (etudiant.erreur != null) {
                mPasswordView.setError(getString(R.string.error_invalid_pwd));
                mPasswordView.requestFocus();
            } else {
                Log.v("LoginActivity", "LoginActivity: o=" + o);
                ApplicationManager.userCredentials = userCredentials;

                new AuthentificationPortailTask().execute(
                        getString(R.string.portail_api_authentification_url),
                        ApplicationManager.userCredentials.getUsername(),
                        ApplicationManager.userCredentials.getPassword());

            }

        } else {
            mPasswordView.setError(getString(R.string.error_invalid_email));
            mPasswordView.requestFocus();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        dataManager.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dataManager.stop();
    }

    private class AuthentificationPortailTask extends AsyncTask<String, Void, Intent> {
        protected Intent doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();

            String url = params[0], username = params[1], password = params[2];

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\n  \"Username\": \"" + username + "\",\n  \"Password\": \"" + password + "\"\n}");
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("content-type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .build();

            Response response = null;
            String authCookie = "";

            try {
                response = client.newCall(request).execute();
                authCookie = response.header("Set-Cookie");
            } catch (IOException e) {
                e.printStackTrace();
            }

            final Intent res = new Intent();
            res.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
            res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, "ca.etsmtl.applets.etsmobile");
            res.putExtra(AccountManager.KEY_AUTHTOKEN, authCookie);

            res.putExtra(Constants.PARAM_USER_PASS, password);
            return res;
        }

        protected void onPostExecute(Intent intent) {

            String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

            String accountPassword = intent.getStringExtra(Constants.PARAM_USER_PASS);
            final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
            if (getIntent().getBooleanExtra(Constants.KEY_IS_ADDING_NEW_ACCOUNT, false)) {
                String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);

                // Creating the account on the device and setting the auth token we got
                // (Not setting the auth token will cause another call to the server to authenticate the user)
                accountManager.addAccountExplicitly(account, accountPassword, null);
                accountManager.setAuthToken(account, Constants.AUTH_TOKEN_TYPE, authtoken);
            } else {
                accountManager.setPassword(account, accountPassword);
            }

            setAccountAuthenticatorResult(intent.getExtras());
            setResult(RESULT_OK, intent);
            finish();

            finishActivity(1);
            startActivity(new Intent(LoginActivity.this, MainActivity.class));

        }

    }

}
