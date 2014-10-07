package ca.etsmtl.applets.etsmobile.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
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

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.model.Etudiant;
import ca.etsmtl.applets.etsmobile.model.UserCredentials;
import ca.etsmtl.applets.etsmobile.util.SecurePreferences;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity implements RequestListener<Object> {

	/**
	 * The default email to populate the email field with.
	 */
	// public static final String EXTRA_EMAIL =
	// "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	// private UserLoginTask mAuthTask = null;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		dataManager = DataManager.getInstance(getApplicationContext());

		setContentView(R.layout.activity_login);

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
				// allow
				// for very easy animations. If available, use these APIs to
				// fade-in
				// the progress spinner.
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
					// The ViewPropertyAnimator APIs are not available, so
					// simply show
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

                SecurePreferences securePreferences = new SecurePreferences(this);
                securePreferences.edit().putString(userCredentials.CODE_U, userCredentials.getUsername()).commit();
                securePreferences.edit().putString(userCredentials.CODE_P, userCredentials.getPassword()).commit();


//				Editor edit = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
//
//				edit.putString(UserCredentials.CODE_U, userCredentials.getUsername());
//				edit.putString(UserCredentials.CODE_P, userCredentials.getPassword());
//				edit.commit();
				finishActivity(1);
				startActivity(new Intent(this, MainActivity.class));
				finish();
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
}
