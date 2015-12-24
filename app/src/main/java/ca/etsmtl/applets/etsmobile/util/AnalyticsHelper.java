package ca.etsmtl.applets.etsmobile.util;

import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import ca.etsmtl.applets.etsmobile2.R;

public class AnalyticsHelper {


    public static final String FONCTIONALITE_UTILISEE = "Fonctionalité utilisée";

    private static AnalyticsHelper instance;
    private static GoogleAnalytics analytics;
    private static Tracker tracker;

    public AnalyticsHelper(Context context) {
        Resources res = context.getResources();

        analytics = GoogleAnalytics.getInstance(context);
        tracker = analytics.newTracker(res.getString(R.string.global_tracker));
        tracker.enableExceptionReporting(true);

        tracker.enableAdvertisingIdCollection(false);
        tracker.enableExceptionReporting(true);
        tracker.enableAutoActivityTracking(true);
    }

    public static synchronized AnalyticsHelper getInstance(Context context) {
        if (instance == null) {
            instance = new AnalyticsHelper(context);
        }
        return instance;
    }

    public void sendScreenEvent(String screenName) {
        tracker.setScreenName(screenName);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void sendActionEvent(String screenName, String action) {
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(FONCTIONALITE_UTILISEE)
                .setAction(action).setLabel(screenName)
                .build());
    }
}
