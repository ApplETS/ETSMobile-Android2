package ca.etsmtl.applets.etsmobilenotifications;

import android.content.Context;
import android.content.Intent;

import com.securepreferences.SecurePreferences;

public final class NotificationsLoginManager {
    /**
     * Call this method when the user has logged in
     *
     * @param context Context
     * @param userName The user name (universal code)
     * @param monEtsDomaine MonÉTS' domain
     */
    public static void login(Context context, String userName, String monEtsDomaine) {
        SecurePreferences.Editor editor = getPrefsEditor(context);

        editor.putString(Constants.USER_NAME_PREF_KEY, userName);
        editor.putString(Constants.MON_ETS_DOMAINE_PREF_KEY, monEtsDomaine);
        editor.putBoolean(Constants.USER_LOGGED_IN_PREF_KEY, true);
        editor.apply();

        FcmRegistrationIntentService.enqueueWork(context, new Intent());
    }

    /**
     * Call this method when the user has logged out
     *
     * @param context Context
     */
    public static void logout(Context context) {
        ArnEndpointHandler handler = new ArnEndpointHandler(context, "", "");

        handler.deleteEndpoint();

        SecurePreferences.Editor editor = getPrefsEditor(context);

        editor.putBoolean(Constants.USER_LOGGED_IN_PREF_KEY, false);
        editor.apply();
    }

    private static SecurePreferences.Editor getPrefsEditor(Context context) {
        SecurePreferences securePreferences = new SecurePreferences(context);

        return securePreferences.edit();
    }

    /**
     * Returns true if the user is logged in
     *
     * @param context Context
     * @return True if the user is logged in
     */
    static boolean isUserLoggedIn(Context context) {
        SecurePreferences securePreferences = new SecurePreferences(context);

        return securePreferences.getBoolean(Constants.USER_LOGGED_IN_PREF_KEY, false);
    }

    /**
     * Get the user name (universal code)
     *
     * @return The user name
     */
    static String getUserName(Context context) {
        SecurePreferences securePreferences = new SecurePreferences(context);

        return securePreferences.getString(Constants.USER_NAME_PREF_KEY, null);
    }

    /**
     * Get MonETS' domain
     *
     * @return MonETS' domain
     */
    static String getMonEtsDomaine(Context context) {
        SecurePreferences securePreferences = new SecurePreferences(context);

        return securePreferences.getString(Constants.MON_ETS_DOMAINE_PREF_KEY, null);
    }
}
