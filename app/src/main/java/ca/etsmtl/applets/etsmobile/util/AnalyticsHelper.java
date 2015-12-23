package ca.etsmtl.applets.etsmobile.util;

import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import ca.etsmtl.applets.etsmobile2.R;

public class AnalyticsHelper {


    public static final String FENETRE = "Fenetre";
    public static final String OUVERTURE = "Ouverture";
    private static AnalyticsHelper instance;
    private static GoogleAnalytics analytics;
    private static Tracker tracker;

    public AnalyticsHelper(Context context) {
        analytics = GoogleAnalytics.getInstance(context);
        Resources res = context.getResources();
        tracker = analytics.newTracker(res.getString(R.string.global_tracker));
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

   /* public static Tracker getTracker() {
        return tracker;
    }*/

    public void sendScreenEvent(String screenName){
        tracker.send(new HitBuilders.EventBuilder(FENETRE, OUVERTURE).setLabel(screenName).build());
    }
}
