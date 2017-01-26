package ca.etsmtl.applets.etsmobile.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Locale;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.ui.activity.LoginActivity;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Implémentation du widget Today.
 */
public class TodayWidgetProvider extends AppWidgetProvider {


    private static final String ACTION_REFRESH = "Refresh";

    private boolean mUserLoggedIn;
    private static int widgetLayoutId = R.layout.widget_today;
    private static int mRefreshBtnId = R.id.widget_refresh_btn;
    private static int progressBarId = R.id.widget_progress_bar;
    private static int todayListId = R.id.widget_todays_list;
    private static int emptyViewId = R.id.widget_empty_view;
    private static int todayNameTvId = R.id.widget_todays_name;

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), widgetLayoutId);

        views.setViewVisibility(progressBarId, View.VISIBLE);
        views.setViewVisibility(mRefreshBtnId, View.GONE);

        // Vue affichée lorsque la liste est vide
        views.setEmptyView(todayListId, emptyViewId);

        if (mUserLoggedIn) {
            Intent intent = new Intent(context, TodayWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            views.setTextViewText(emptyViewId, context.getString(R.string.today_no_classes));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, todayListId);
            views.setRemoteAdapter(appWidgetId, todayListId, intent);
            views.setViewVisibility(emptyViewId, View.VISIBLE);
            setUpRefreshBtn(context, views);
        } else {
            views.setViewVisibility(todayListId, View.GONE);
            views.setViewVisibility(emptyViewId, View.GONE);
        }

        setUpTodayDateTv(context, views);
        setUpLoginBtn(context, views);

        views.setViewVisibility(progressBarId, View.GONE);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private void setUpLoginBtn(Context context, RemoteViews views) {
        int loginBtnId = R.id.widget_login_btn;

        if (mUserLoggedIn) {
            views.setViewVisibility(loginBtnId, View.GONE);
        } else {
            Intent intentConnexion = new Intent(context, LoginActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentConnexion, 0);
            views.setOnClickPendingIntent(loginBtnId, pendingIntent);
            views.setTextViewText(loginBtnId, context.getString(R.string.touch_login));
            views.setViewVisibility(loginBtnId, View.VISIBLE);
        }
    }

    private void setUpTodayDateTv(Context context, RemoteViews views) {

        if (mUserLoggedIn) {
            DateTime dateActuelle = new DateTime();
            DateTime.Property pDoW = dateActuelle.dayOfWeek();
            DateTime.Property pDoM = dateActuelle.dayOfMonth();
            DateTime.Property pMoY = dateActuelle.monthOfYear();
            Locale locale = context.getResources().getConfiguration().locale;
            String dateActuelleStr = context.getString(R.string.horaire, pDoW.getAsText(locale),
                    pDoM.getAsText(locale), pMoY.getAsText(locale));
            views.setTextViewText(todayNameTvId, dateActuelleStr);
            views.setViewVisibility(todayNameTvId, View.VISIBLE);
        } else {
            views.setViewVisibility(todayNameTvId, View.GONE);
        }
    }

    private void setUpRefreshBtn(Context context, RemoteViews views) {
        Intent intentRefresh = new Intent(context,  TodayWidgetProvider.class);
        intentRefresh.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intentRefresh.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetsIds(context));
        PendingIntent pendingIntentRefresh = PendingIntent.getBroadcast(context, 0, intentRefresh,
                PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(mRefreshBtnId, pendingIntentRefresh);
        views.setViewVisibility(mRefreshBtnId, View.VISIBLE);
    }

    private boolean userLoggedIn() {
        return ApplicationManager.userCredentials != null;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        mUserLoggedIn = userLoggedIn();

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String actionStr = intent.getAction();

        if (actionStr != null && actionStr.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            int[] widgetsIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            if (widgetsIds == null || widgetsIds.length == 0)
                widgetsIds = allWidgetsIds(context);

            onUpdate(context, appWidgetManager, widgetsIds);
        }
    }

    public static void updateAllWidgets(Context context) {
        Intent intent = new Intent(context, TodayWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetsIds(context));
        context.sendBroadcast(intent);
    }

    private static int[] allWidgetsIds(Context context) {
        ComponentName componentName = new ComponentName(context, TodayWidgetProvider.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        return appWidgetManager.getAppWidgetIds(componentName);
    }
}

