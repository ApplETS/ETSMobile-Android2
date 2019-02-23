package ca.etsmtl.applets.etsmobile.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import androidx.core.content.ContextCompat;
import ca.etsmtl.applets.etsmobile2.R;

public class Utility {

    public static boolean isTabletDevice(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;

    }

    public static boolean isNetworkAvailable(final Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    /**
     * Return Date from string "yyyy-MM-dd"
     *
     * @param dateString
     * @return
     */
    public static Date getDateFromString(String dateString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getStringForApplETSApiFromDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy");
        return simpleDateFormat.format(date);
    }

    /**
     * Conversion d'une heure Unix en objet {@link Date}
     *
     * @param unixDate heure Unix (nombre de secondes écoulées depuis le 1er janvier 1970 00:00:00
     *                 UTC)
     * @return objet {@link Date} représentant l'heure unix
     */
    public static Date getDateTimeFromUnixTime(long unixDate) {
        return new Date(TimeUnit.SECONDS.toMillis(unixDate));
    }

    public static Map<String, String> parseCookies(String cookieHeader) {
        Map<String, String> result = new LinkedHashMap<String, String>();
        if (cookieHeader != null) {
            String[] cookiesRaw = cookieHeader.split("; ");
            for (int i = 0; i < cookiesRaw.length; i++) {
                String[] parts = cookiesRaw[i].split("=", 2);
                String value = parts.length > 1 ? parts[1] : "";
                if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
                result.put(parts[0], value);
            }
        }
        return result;
    }

    public static Date getDate(final SecurePreferences prefs, final String key, final Date defValue) {
        if (!prefs.contains(key + "_value")) {
            return defValue;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(prefs.getLong(key + "_value", 0));
        return calendar.getTime();
    }

    public static void putDate(final SecurePreferences prefs, final String key, final Date date) {
        prefs.edit().putLong(key + "_value", date.getTime()).commit();
    }

    public static void saveCookieExpirationDate(String cookie, SecurePreferences securePreferences) {

        Map<String, String> parsedCookie = Utility.parseCookies(cookie);
        String expires = parsedCookie.get("expires");

        Date expirationDate = new Date();
        SimpleDateFormat df = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss z", Locale.US);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            expirationDate = df.parse(expires);
            Utility.putDate(securePreferences, Constants.EXP_DATE_COOKIE, expirationDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    /**
     * Returns a color for a given string.
     *
     * @param str
     * @param transparency between 0 (transparent) and 255 (opaque)
     * @return
     */
    public static int stringToColour(String str, int transparency) {
        if (transparency < 0) {
            transparency = 0;
        }

        if (transparency > 255) {
            transparency = 255;
        }

        if(TextUtils.isEmpty(str)) {
            str = "default";
        }

        int hash = str.hashCode();

        int r = (hash & 0xFF0000) >> 16;
        int g = (hash & 0x00FF00) >> 8;
        int b = hash & 0x0000FF;

        return Color.argb(transparency, r, g, b);
    }

    public static void openChromeCustomTabs(Context context, String url) {
        openChromeCustomTabs(context, url, true);
    }

    public static void openChromeCustomTabs(Context context, String url, boolean showTitle) {

        if (!URLUtil.isValidUrl(url)) {
            Log.w("Utility", "Url not valid!");
            Log.w("Utility", url);
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        Bundle extras = new Bundle();

        // Something needs to be done about the min SDK...
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            extras.putBinder(Constants.EXTRA_CUSTOM_TABS_SESSION, null);
            extras.putInt(Constants.EXTRA_CUSTOM_TABS_TOOLBAR_COLOR, ContextCompat.getColor(context, R.color.ets_red_fonce));

            if (showTitle) {
                extras.putInt(Constants.EXTRA_CUSTOM_TABS_TITLE_VISIBILITY_STATE, Constants.EXTRA_CUSTOM_TABS_SHOW_TITLE);
            }
        }
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    public static void goToAppSettings(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }

}
