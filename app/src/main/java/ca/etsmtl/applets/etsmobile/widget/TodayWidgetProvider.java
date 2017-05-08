package ca.etsmtl.applets.etsmobile.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.support.v4.graphics.ColorUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

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
import ca.etsmtl.applets.etsmobile.model.UserCredentials;
import ca.etsmtl.applets.etsmobile.ui.activity.LoginActivity;
import ca.etsmtl.applets.etsmobile.util.Constants;
import ca.etsmtl.applets.etsmobile.util.HoraireManager;
import ca.etsmtl.applets.etsmobile.util.TrimestreComparator;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Implémentation du widget Today.
 */
public class TodayWidgetProvider extends AppWidgetProvider implements RequestListener<Object>, Observer {

    private static int widgetInitialLayoutId = R.layout.widget_today;
    private static int widgetLayoutId = R.id.today_widget;
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

        RemoteViews views = new RemoteViews(context.getPackageName(), widgetInitialLayoutId);

        if (!syncEnCours && mUserLoggedIn) {
            views.setViewVisibility(syncBtnId, View.VISIBLE);
            views.setViewVisibility(progressBarId, View.GONE);
        } else if (syncEnCours && mUserLoggedIn){
            views.setViewVisibility(progressBarId, View.VISIBLE);
            views.setProgressBar(progressBarId, 0, 0, true);
            views.setViewVisibility(syncBtnId, View.GONE);
        }

        int bgColor = TodayWidgetConfigureActivity.loadBgColorPref(context, appWidgetId);
        int textColor = TodayWidgetConfigureActivity.loadTextColorPref(context, appWidgetId);
        int bgOpacity = TodayWidgetConfigureActivity.loadOpacityPref(context, appWidgetId);

        // Vue affichée lorsque la liste est vide
        views.setTextColor(emptyViewId, textColor);
        views.setEmptyView(todayListId, emptyViewId);

        if (mUserLoggedIn) {
            views.setTextColor(todayNameTvId, textColor);
            Intent intent = new Intent(context, TodayWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.putExtra(Constants.TEXT_COLOR, textColor);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            views.setTextViewText(emptyViewId, context.getString(R.string.today_no_classes));
            views.setRemoteAdapter(appWidgetId, todayListId, intent);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, todayListId);
            views.setViewVisibility(emptyViewId, View.VISIBLE);
            setUpSyncBtn(context, views, textColor);
        } else {
            views.setViewVisibility(syncBtnId, View.GONE);
            views.setViewVisibility(progressBarId, View.GONE);
            views.setViewVisibility(todayListId, View.GONE);
            views.setViewVisibility(emptyViewId, View.GONE);
        }

        setUpTodayDateTv(context, views);
        setUpLoginBtn(context, views, textColor);

        bgColor = ColorUtils.setAlphaComponent(bgColor, bgOpacity);
        views.setInt(widgetLayoutId, "setBackgroundColor", bgColor);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        this.context = context;
        horaireManager = new HoraireManager(this, context);
        horaireManager.addObserver(this);

        mUserLoggedIn = userLoggedIn();

        this.appWidgetManager = appWidgetManager;

        if (mUserLoggedIn && !syncEnCours) {
            dataManager = DataManager.getInstance(context);
            sync();
            syncEnCours = true;
            // Sauvegarde des ids pour une mise à jour ultérieure à la suite de la synchronisation
            appWidgetsToBeUpdatedIds = appWidgetIds.clone();
        }

        String lang = TodayWidgetConfigureActivity.loadLanguagePref(context, appWidgetIds[0]);
        setAllWidgetsLocale(context, lang);

        // Mise à jour de chaque widget avec les données locales en attendant les données distantes
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
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preferences associated with it.
        for (int appWidgetId : appWidgetIds) {
            TodayWidgetConfigureActivity.deleteAllPreferences(context, appWidgetId);
        }
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

    private void setUpLoginBtn(Context context, RemoteViews views, int textColor) {
        int loginBtnId = R.id.widget_login_btn;

        if (mUserLoggedIn) {
            views.setViewVisibility(loginBtnId, View.GONE);
        } else {
            Intent intentLogin = new Intent(context, LoginActivity.class);
            intentLogin.putExtra(Constants.KEY_IS_ADDING_NEW_ACCOUNT, true);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentLogin, 0);
            views.setOnClickPendingIntent(loginBtnId, pendingIntent);
            views.setTextViewText(loginBtnId, context.getString(R.string.touch_login));
            views.setTextColor(loginBtnId, textColor);
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

    private void setUpSyncBtn(Context context, RemoteViews views, int textColor) {
        Intent intentRefresh = new Intent(context,  TodayWidgetProvider.class);
        intentRefresh.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intentRefresh.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetsIds(context));
        PendingIntent pendingIntentRefresh = PendingIntent.getBroadcast(context, 0, intentRefresh,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_sync);

        // Copie mutable de l'icône
        icon = icon.copy(Bitmap.Config.ARGB_8888, true);
        Paint paint = new Paint();
        ColorFilter filter = new PorterDuffColorFilter(textColor, PorterDuff.Mode.SRC_IN);
        paint.setColorFilter(filter);
        Canvas canvas = new Canvas(icon);
        canvas.drawBitmap(icon, 0, 0, paint);

        views.setImageViewBitmap(syncBtnId, icon);
        views.setInt(syncBtnId, "setBackgroundColor", Color.TRANSPARENT);
        views.setOnClickPendingIntent(syncBtnId, pendingIntentRefresh);
    }

    private void setAllWidgetsLocale(Context context, String language) {
        Configuration configuration = new Configuration();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        Locale locale;

        if (language.equalsIgnoreCase("en")) {
            locale = Locale.ENGLISH;
        } else if (language.equalsIgnoreCase("fr"))
            locale = Locale.CANADA_FRENCH;
        else
            locale = Locale.getDefault();

        Locale.setDefault(locale);
        configuration.locale = locale;
        context.getResources().updateConfiguration(configuration, displayMetrics);
    }

    private boolean userLoggedIn() {
        return ApplicationManager.userCredentials != null;
    }

    private void sync() {
        // Requêtes des données distantes
        dataManager.start();
        dataManager.getDataFromSignet(DataManager.SignetMethods.LIST_SESSION, ApplicationManager.userCredentials, this);
        dataManager.getDataFromSignet(DataManager.SignetMethods.LIST_SEANCES_CURRENT_AND_NEXT_SESSION, ApplicationManager.userCredentials, this);
        dataManager.getDataFromSignet(DataManager.SignetMethods.LIST_JOURSREMPLACES_CURRENT_AND_NEXT_SESSION, ApplicationManager.userCredentials, this);
    }

    /**
     * Procédure appelée par {@link ca.etsmtl.applets.etsmobile.http.DataManager#getDataFromSignet(int, UserCredentials, RequestListener, String...)}
     * en cas d'échec
     *
     * @param spiceException
     */
    @Override
    public void onRequestFailure(SpiceException spiceException) {
        syncEnCours = false;

        Toast toast = Toast.makeText(context, "ÉTSMobile" + context.getString(R.string.deux_points)
                + context.getString(R.string.toast_Sync_Fail), Toast.LENGTH_SHORT);
        toast.show();

        // Mise à jour avec les données locales
        for (int appWidgetId : appWidgetsToBeUpdatedIds) {

            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    /**
     * Procédure appelée par {@link ca.etsmtl.applets.etsmobile.http.DataManager#getDataFromSignet(int, UserCredentials, RequestListener, String...)}
     * en cas de succès
     *
     * @param o
     */
    @Override
    public void onRequestSuccess(Object o) {
        if (o instanceof ListeDeSessions) {
            requestEventList((ListeDeSessions) o);
        } else {
            // Mise à jour de la BD contenant les données locales
            horaireManager.onRequestSuccess(o);
        }
    }

    /**
     * Procédure déclenchant une requête additionnelle pour permettre la synchronisation de la liste
     * d'événements et satisfaire la condition syncEventListEnded dans
     * {@link ca.etsmtl.applets.etsmobile.util.HoraireManager#onRequestSuccess(Object)}
     *
     * @param listeDeSessions
     */
    private void requestEventList(ListeDeSessions listeDeSessions) {
        Trimestre derniereSession = Collections.max(listeDeSessions.liste,
                new TrimestreComparator());

        DateTime dateDebut = new DateTime(derniereSession.dateDebut);

        if(DateTime.now().isBefore(dateDebut)) {
            dateDebut = DateTime.now();
        }

        DateTime dateEnd = new DateTime(derniereSession.dateFin);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateDebutFormatted = formatter.format(dateDebut.toDate());
        String dateFinFormatted = formatter.format(dateEnd.toDate());
        dataManager.start();
        dataManager.sendRequest(new AppletsApiCalendarRequest(context, dateDebutFormatted,
                dateFinFormatted), this);
    }

    /**
     * <h1>Mise à jour des widgets</h1>
     *
     * À cette étape, les données distantes ont été obtenues et la BD locale a été mise à jour dans
     * {@link ca.etsmtl.applets.etsmobile.util.HoraireManager#onRequestSuccess(Object)} permettant
     * ainsi de déclencher la mise la jour des widgets
     */
    @Override
    public void update(Observable observable, Object data) {
        syncEnCours = false;

        for (int appWidgetId : appWidgetsToBeUpdatedIds) {

            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    /**
     * <h1>Mise à jour d'un widget</h1>
     *
     * Procédure pouvant être appelée dans l'application principale afin de déclencher la mise à
     * jour d'un widget
     *
     * @param context
     */
    public static void updateWidget(Context context, int appWidgetId) {
        Intent intent = new Intent(context, TodayWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] {appWidgetId});
        context.sendBroadcast(intent);
    }

    /**
     * <h1>Mise à jour de tous les widgets</h1>
     *
     * Procédure pouvant être appelée dans l'application principale afin de déclencher la mise à
     * jour de tous les widgets
     *
     * @param context
     */
    public static void updateAllWidgets(Context context) {
        Intent intent = new Intent(context, TodayWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetsIds(context));
        context.sendBroadcast(intent);
    }

    public static void setAllWidgetsLanguage(Context context, String language) {
        for (int id : allWidgetsIds(context)) {
            TodayWidgetConfigureActivity.saveLanguagePref(context, id, language);
        }
    }

    private static int[] allWidgetsIds(Context context) {
        ComponentName componentName = new ComponentName(context, TodayWidgetProvider.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        return appWidgetManager.getAppWidgetIds(componentName);
    }
}

