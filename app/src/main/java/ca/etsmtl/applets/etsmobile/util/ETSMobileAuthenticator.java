package ca.etsmtl.applets.etsmobile.util;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.service.RegistrationIntentService;
import ca.etsmtl.applets.etsmobile2.R;

import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;

/**
 * Created by gnut3ll4 on 15/10/15.
 */
public class ETSMobileAuthenticator extends AbstractAccountAuthenticator {
    private final Context mContext;
    private final Class loginActivity;

    public ETSMobileAuthenticator(Context context, Class loginActivity) {
        super(context);

        this.mContext = context;
        this.loginActivity = loginActivity;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String s) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        final Intent intent = new Intent(mContext, loginActivity);
        intent.putExtra(Constants.KEY_ACCOUNT_TYPE, accountType);
        intent.putExtra(Constants.KEY_AUTH_TYPE, authTokenType);
        intent.putExtra(Constants.KEY_IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        final AccountManager am = AccountManager.get(mContext);

        String authToken = am.peekAuthToken(account, authTokenType);

        SecurePreferences securePreferences = new SecurePreferences(mContext);
        Date expirationDate = Utility.getDate(securePreferences, Constants.EXP_DATE_COOKIE, new Date());
        Date now = new Date();

        // Lets give another try to authenticate the user
        if (TextUtils.isEmpty(authToken) || expirationDate.before(now)) {
            final String password = am.getPassword(account);
            final String username = account.name;

            if (password != null) {

                OkHttpClient client = new OkHttpClient();

                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\n  \"Username\": \"" + username + "\",\n  \"Password\": \"" + password + "\"\n}");
                Request request = new Request.Builder()
                        .url(mContext.getString(R.string.portail_api_authentification_url))
                        .post(body)
                        .addHeader("content-type", "application/json")
                        .addHeader("cache-control", "no-cache")
                        .build();

                Response httpResponse = null;

                try {
                    httpResponse = client.newCall(request).execute();
                    if (httpResponse.code() == 200) {
                        authToken = httpResponse.header("Set-Cookie");

                        Utility.saveCookieExpirationDate(authToken, securePreferences);

                        JSONObject jsonResponse = new JSONObject(httpResponse.body().string());

                        int typeUsagerId = jsonResponse.getInt("TypeUsagerId");
                        String domaine = jsonResponse.getString("Domaine");

                        securePreferences.edit().putInt(Constants.TYPE_USAGER_ID, typeUsagerId).commit();
                        securePreferences.edit().putString(Constants.DOMAINE, domaine).commit();
                        ApplicationManager.domaine = domaine;
                        ApplicationManager.typeUsagerId = typeUsagerId;

                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                        boolean isTokenSent = sharedPreferences.getBoolean(Constants.IS_GCM_TOKEN_SENT_TO_SERVER, false);
                        if (!isTokenSent) {
                            Intent intent = new Intent(mContext, RegistrationIntentService.class);
                            mContext.startService(intent);
                        }
                    } else {
                        Log.e("Erreur Portail", httpResponse.toString());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        // If we get an authToken - we return it
        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        } else {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, null);
            return result;
        }


        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity.
        /*
        final Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, account.type);
        intent.putExtra(Constants.KEY_AUTH_TYPE, authTokenType);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
        */
    }

    @Override
    public String getAuthTokenLabel(String s) {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String[] strings) throws NetworkErrorException {
        final Bundle result = new Bundle();
        result.putBoolean(KEY_BOOLEAN_RESULT, false);
        return result;
    }
}
