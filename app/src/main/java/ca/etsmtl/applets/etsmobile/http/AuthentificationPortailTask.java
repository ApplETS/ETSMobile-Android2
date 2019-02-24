package ca.etsmtl.applets.etsmobile.http;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.service.RegistrationIntentService;
import ca.etsmtl.applets.etsmobile.util.Constants;
import ca.etsmtl.applets.etsmobile.util.SecurePreferences;
import ca.etsmtl.applets.etsmobile.util.Utility;
import ca.etsmtl.applets.etsmobile2.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by gnut3ll4 on 04/11/15.
 */
public class AuthentificationPortailTask extends AsyncTask<String, Void, Intent> {

    private final AccountManager accountManager;
    private Activity launchingActivity;

    public AuthentificationPortailTask(Activity launchingActivity) {
        this.launchingActivity = launchingActivity;
        accountManager = AccountManager.get(launchingActivity);
    }

    protected Intent doInBackground(String... params) {
        OkHttpClient client = TLSUtilities.createETSOkHttpClient(launchingActivity);
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
        String authCookie = "", domaine = "";
        int typeUsagerId = 0;

        final Intent res = new Intent();

        try {
            response = client.newCall(request).execute();

            if (response.code() == 200) {

                authCookie = response.header("Set-Cookie");

                JSONObject jsonResponse = new JSONObject(response.body().string());

                typeUsagerId = jsonResponse.getInt("TypeUsagerId");
                domaine = jsonResponse.getString("Domaine");

                res.putExtra(AccountManager.KEY_AUTHTOKEN, authCookie);
                res.putExtra(Constants.TYPE_USAGER_ID, typeUsagerId);
                res.putExtra(Constants.DOMAINE, domaine);
            } else {
                Log.e("Erreur Portail", response.toString());
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        res.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
        res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);

        res.putExtra(Constants.PARAM_USER_PASS, password);


        return res;
    }

    protected void onPostExecute(Intent intent) {

        if (intent != null) {

            Account[] accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
            if (accounts.length > 0) {

                String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);

                if (!TextUtils.isEmpty(authtoken)) {
                    int typeUsagerId = intent.getIntExtra(Constants.TYPE_USAGER_ID, -1);
                    String domaine = intent.getStringExtra(Constants.DOMAINE);

                    SecurePreferences securePreferences = new SecurePreferences(launchingActivity);
                    securePreferences.edit().putInt(Constants.TYPE_USAGER_ID, typeUsagerId).commit();
                    securePreferences.edit().putString(Constants.DOMAINE, domaine).commit();

                    securePreferences.edit().putString(Constants.EXP_DATE_COOKIE, domaine).commit();
                    ApplicationManager.domaine = domaine;
                    ApplicationManager.typeUsagerId = typeUsagerId;
                    accountManager.setAuthToken(accounts[0], Constants.AUTH_TOKEN_TYPE, authtoken);

                    Utility.saveCookieExpirationDate(authtoken, securePreferences);

                    Intent gcmRegistrationIntent = new Intent(launchingActivity, RegistrationIntentService.class);
                    launchingActivity.startService(gcmRegistrationIntent);
                }
            }


            launchingActivity.finish();


        }

    }
}