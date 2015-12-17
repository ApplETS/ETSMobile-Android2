package ca.etsmtl.applets.etsmobile.util;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by jjghali on 15-11-20.
 */
public class AnalyticsHelper {


    private static AnalyticsHelper instance;
    private static GoogleAnalytics analytics;
    private static Tracker tracker;

    public AnalyticsHelper(Context context) {
        analytics = GoogleAnalytics.getInstance(context);

        tracker = analytics.newTracker(R.string.global_tracker);
        tracker.enableExceptionReporting(true);

        tracker.enableAdvertisingIdCollection(false);

        tracker.enableAutoActivityTracking(true);
    }

    public static AnalyticsHelper getInstance(Context context) {
        if (instance == null){
            instance = new AnalyticsHelper(context);
        }
        return instance;
    }

    public static Tracker getTracker() {
        return tracker;
    }
}
