package ca.etsmtl.applets.etsmobile.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;
import org.joda.time.DateTime;

import java.util.Locale;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.ui.activity.LoginActivity;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Implementation of App Widget functionality.
 */
public class TodayWidgetProvider extends AppWidgetProvider {


    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_today);

        int todayListId = R.id.widget_todays_list;
        int emptyViewId = R.id.widget_empty_view;

        // Vue affichée lorsque la liste est vide
        views.setEmptyView(todayListId, emptyViewId);

        if (userLoggedIn()) {
            Intent intent = new Intent(context, TodayWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            views.setTextViewText(emptyViewId, context.getString(R.string.today_no_classes));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, todayListId);
            views.setRemoteAdapter(appWidgetId, todayListId, intent);
            views.setViewVisibility(emptyViewId, View.VISIBLE);
        } else {
            views.setViewVisibility(todayListId, View.GONE);
            views.setViewVisibility(emptyViewId, View.GONE);
        }
        setUpLoginBtn(context, views);
        setUpTodayDateTv(context, views);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private void setUpLoginBtn(Context context, RemoteViews views) {
        int loginBtnId = R.id.widget_login_btn;

        if (userLoggedIn()) {
            views.setViewVisibility(loginBtnId, View.GONE);
        } else {
            Intent intentConnexion = new Intent(context, LoginActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentConnexion, 0);
            views.setOnClickPendingIntent(loginBtnId, pendingIntent);
            views.setTextViewText(loginBtnId, context.getString(R.string.prompt_login));
            views.setViewVisibility(loginBtnId, View.VISIBLE);
        }
    }

    private void setUpTodayDateTv(Context context, RemoteViews views) {
        int todayNameTvId = R.id.widget_todays_name;

        if (userLoggedIn()) {
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

    private boolean userLoggedIn() {
        return ApplicationManager.userCredentials != null;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
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

    public static void updateAllWidgets(Context context) {
        ComponentName componentName = new ComponentName(context, TodayWidgetProvider.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] ids = appWidgetManager.getAppWidgetIds(componentName);
        Intent intent = new Intent(context, TodayWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }
}

