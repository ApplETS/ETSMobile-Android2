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

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.http.AppletsApiCalendarRequest;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.model.ListeDeSessions;
import ca.etsmtl.applets.etsmobile.model.Trimestre;
import ca.etsmtl.applets.etsmobile.ui.activity.LoginActivity;
import ca.etsmtl.applets.etsmobile.util.HoraireManager;
import ca.etsmtl.applets.etsmobile.util.TrimestreComparator;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Implémentation du widget Today.
 */
public class TodayWidgetProvider extends AppWidgetProvider implements RequestListener<Object>, Observer {

    private static int widgetLayoutId = R.layout.widget_today;
    private static int syncBtnId = R.id.widget_sync_btn;
    private static int progressBarId = R.id.widget_progress_bar;
    private static int todayListId = R.id.widget_todays_list;
    private static int emptyViewId = R.id.widget_empty_view;
    private static int todayNameTvId = R.id.widget_todays_name;
    private static DataManager dataManager;

    private HoraireManager horaireManager;
    private Context context;
    private boolean mUserLoggedIn;
    private boolean syncEnCours;
    private int[] appWidgetsToBeUpdatedIds;
    private AppWidgetManager appWidgetManager;

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), widgetLayoutId);

        if (!syncEnCours && mUserLoggedIn) {
            views.setViewVisibility(syncBtnId, View.VISIBLE);
            views.setViewVisibility(progressBarId, View.GONE);
        } else if (syncEnCours && mUserLoggedIn){
            views.setViewVisibility(progressBarId, View.VISIBLE);
            views.setViewVisibility(syncBtnId, View.GONE);
        } else if (!mUserLoggedIn) {
            views.setViewVisibility(syncBtnId, View.GONE);
            views.setViewVisibility(progressBarId, View.GONE);
        }

        // Vue affichée lorsque la liste est vide
        views.setEmptyView(todayListId, emptyViewId);

        if (mUserLoggedIn) {
            Intent intent = new Intent(context, TodayWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            views.setTextViewText(emptyViewId, context.getString(R.string.today_no_classes));
            views.setRemoteAdapter(appWidgetId, todayListId, intent);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, todayListId);
            views.setViewVisibility(emptyViewId, View.VISIBLE);
            setUpRefreshBtn(context, views);
        } else {
            views.setViewVisibility(todayListId, View.GONE);
            views.setViewVisibility(emptyViewId, View.GONE);
        }

        setUpTodayDateTv(context, views);
        setUpLoginBtn(context, views);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        this.context = context;
        horaireManager = new HoraireManager(this, context);
        horaireManager.addObserver(this);

        // There may be multiple widgets active, so update all of them
        mUserLoggedIn = userLoggedIn();

        this.appWidgetManager = appWidgetManager;

        if (mUserLoggedIn && !syncEnCours) {
            dataManager = DataManager.getInstance(context);
            sync();
            syncEnCours = true;
            // Sauvegarde des ids pour une mise à jour ultérieure à la suite de la synchronisation
            appWidgetsToBeUpdatedIds = appWidgetIds.clone();
        }

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

        views.setOnClickPendingIntent(syncBtnId, pendingIntentRefresh);
    }

    private boolean userLoggedIn() {
        return ApplicationManager.userCredentials != null;
    }

    private void sync() {
        // Requêtes des données distantes
        dataManager.getDataFromSignet(DataManager.SignetMethods.LIST_SESSION, ApplicationManager.userCredentials, this);
        dataManager.getDataFromSignet(DataManager.SignetMethods.LIST_SEANCES_CURRENT_AND_NEXT_SESSION, ApplicationManager.userCredentials, this);
        dataManager.getDataFromSignet(DataManager.SignetMethods.LIST_JOURSREMPLACES_CURRENT_AND_NEXT_SESSION, ApplicationManager.userCredentials, this);
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        syncEnCours = false;

        // Mise à jour avec les données locales
        for (int appWidgetId : appWidgetsToBeUpdatedIds) {

            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onRequestSuccess(Object o) {
        if (o instanceof ListeDeSessions) {
            requestEventList((ListeDeSessions) o);
        }

        // Mise à jour des données locales
        horaireManager.onRequestSuccess(o);
    }

    /**
     * Requête permettant de satisfaire la condition syncEventListEnded de horaireManager.
     *
     * @param listeDeSessions
     */
    private void requestEventList(ListeDeSessions listeDeSessions) {
        Trimestre derniereSession = Collections.max(listeDeSessions.liste, new TrimestreComparator());

        DateTime dateDebut = new DateTime(derniereSession.dateDebut);

        if(DateTime.now().isBefore(dateDebut)) {
            dateDebut = DateTime.now();
        }

        DateTime dateEnd = new DateTime(derniereSession.dateFin);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateDebutFormatted = formatter.format(dateDebut.toDate());
        String dateFinFormatted = formatter.format(dateEnd.toDate());
        dataManager.sendRequest(new AppletsApiCalendarRequest(context, dateDebutFormatted,
                dateFinFormatted), this);
    }

    /**
     * This method is called if the specified {@code Observable} object's
     * {@code notifyObservers} method is called (because the {@code Observable}
     * object has been updated.
     *
     * @param observable the {@link Observable} object.
     * @param data       the data passed to {@link Observable#notifyObservers(Object)}.
     */
    @Override
    public void update(Observable observable, Object data) {
        syncEnCours = false;

        for (int appWidgetId : appWidgetsToBeUpdatedIds) {

            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
}

