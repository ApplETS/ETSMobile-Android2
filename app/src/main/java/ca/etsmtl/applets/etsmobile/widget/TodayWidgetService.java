package ca.etsmtl.applets.etsmobile.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.joda.time.DateTime;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.db.DatabaseHelper;
import ca.etsmtl.applets.etsmobile.http.AppletsApiCalendarRequest;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.model.Event;
import ca.etsmtl.applets.etsmobile.model.ListeDeSessions;
import ca.etsmtl.applets.etsmobile.model.Seances;
import ca.etsmtl.applets.etsmobile.ui.adapter.TodayDataRowItem;
import ca.etsmtl.applets.etsmobile.util.HoraireManager;
import ca.etsmtl.applets.etsmobile.util.SeanceComparator;
import ca.etsmtl.applets.etsmobile.util.Utility;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by Sonphil on 2017-01-17.
 */

public class TodayWidgetService extends RemoteViewsService {
    /**
     * To be implemented by the derived service to generate appropriate factories for
     * the data.
     *
     * @param intent
     */
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewFactory(this.getApplicationContext(), intent);
    }

    private class ListRemoteViewFactory implements RemoteViewsFactory, RequestListener<Object>, Observer {

        private List<Seances> listeSeances;
        private List<Event> listeEvents;
        private List<TodayDataRowItem> listeDataRowItems;
        private Context context;
        private int appWidgetId;
        private DatabaseHelper databaseHelper;
        private HoraireManager horaireManager;
        private DataManager dataManager;

        public ListRemoteViewFactory(Context context, Intent intent) {
            this.context = context;
            this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            listeDataRowItems = new ArrayList<TodayDataRowItem>();
            horaireManager = new HoraireManager(this, context);
            horaireManager.addObserver(this);
            dataManager = DataManager.getInstance(context);
        }

        /**
         * Called when your factory is first constructed. The same factory may be shared across
         * multiple RemoteViewAdapters depending on the intent passed.
         */
        @Override
        public void onCreate() {
            // Affichage des données locaux actuelles
            databaseHelper = new DatabaseHelper(context);
            updateUI();

            // Requêtes d'obtention des données distantes
            dataManager.getDataFromSignet(DataManager.SignetMethods.LIST_SESSION, ApplicationManager.userCredentials, this);
            dataManager.getDataFromSignet(DataManager.SignetMethods.LIST_SEANCES_CURRENT_AND_NEXT_SESSION, ApplicationManager.userCredentials, this);
            dataManager.getDataFromSignet(DataManager.SignetMethods.LIST_JOURSREMPLACES_CURRENT_AND_NEXT_SESSION, ApplicationManager.userCredentials, this);
        }

        /**
         * Mise à jour avec les données locaux
         */
        private void updateUI() {
            listeSeances = new ArrayList<Seances>();
            listeEvents = new ArrayList<Event>();
            listeDataRowItems = new ArrayList<TodayDataRowItem>();

            try {
                DateTime dateTime = new DateTime();
                dateTime = dateTime.plusDays(4);

                SimpleDateFormat seancesFormatter = new SimpleDateFormat("yyyy-MM-dd",
                        context.getResources().getConfiguration().locale);

                String dateStrFormatted = seancesFormatter.format(dateTime.toDate()).toString();

                listeSeances = databaseHelper.getDao(Seances.class).queryBuilder().where().
                        like("dateDebut", dateStrFormatted + "%").query();
                Collections.sort(listeSeances, new SeanceComparator());
                listeEvents = databaseHelper.getDao(Event.class).queryBuilder().where().
                        like("startDate", dateStrFormatted + "%").query();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (listeSeances != null && !listeSeances.isEmpty()) {
                for (Seances seances : listeSeances) {
                    listeDataRowItems.add(new TodayDataRowItem(TodayDataRowItem.viewType.VIEW_TYPE_SEANCE, seances));
                }
            }

            if (listeEvents != null && !listeEvents.isEmpty()) {
                listeDataRowItems.add(new TodayDataRowItem(TodayDataRowItem.viewType.VIEW_TYPE_TITLE_EVENT));
                for (Event event : listeEvents) {
                    listeDataRowItems.add(new TodayDataRowItem(TodayDataRowItem.viewType.VIEW_TYPE_EVENT, event));
                }
            }
        }

        /**
         * Called when notifyDataSetChanged() is triggered on the remote adapter. This allows a
         * RemoteViewsFactory to respond to data changes by updating any internal references.
         * <p>
         * Note: expensive tasks can be safely performed synchronously within this method. In the
         * interim, the old data will be displayed within the widget.
         *
         * @see AppWidgetManager#notifyAppWidgetViewDataChanged(int[], int)
         */
        @Override
        public void onDataSetChanged() {
            onCreate();
        }


        /**
         * Called when the last RemoteViewsAdapter that is associated with this factory is
         * unbound.
         */
        @Override
        public void onDestroy() {

        }

        /**
         * See {@link Adapter#getCount()}
         *
         * @return Count of items.
         */
        @Override
        public int getCount() {
            return listeDataRowItems.size();
        }

        /**
         * See {@link Adapter#getView(int, View, ViewGroup)}.
         * <p>
         * Note: expensive tasks can be safely performed synchronously within this method, and a
         * loading view will be displayed in the interim. See {@link #getLoadingView()}.
         *
         * @param position The position of the item within the Factory's data set of the item whose
         *                 view we want.
         * @return A RemoteViews object corresponding to the data at the specified position.
         */
        @Override
        public RemoteViews getViewAt(int position) {
            if (listeSeances.size() == 0)
                return null;

            RemoteViews rv = null;
            TodayDataRowItem item = listeDataRowItems.get(position);
            String packageName = context.getPackageName();
            int viewType = getItemViewType(position);

            if (viewType == TodayDataRowItem.viewType.VIEW_TYPE_TITLE_EVENT.getValue()) {
                rv = new RemoteViews(packageName, R.layout.row_today_title);
                rv.setTextViewText(R.id.todays_title, getString(R.string.today_event));

            } else if (viewType == TodayDataRowItem.viewType.VIEW_TYPE_EVENT.getValue()) {
                Event event = (Event) item.data;
                rv = new RemoteViews(packageName, R.layout.widget_today_row_event);
                rv.setTextViewText(R.id.event_text, event.getTitle());

            } else if (viewType == TodayDataRowItem.viewType.VIEW_TYPE_TITLE_SEANCE.getValue()) {
                rv = new RemoteViews(packageName, R.layout.row_today_title);
                rv.setTextViewText(R.id.todays_title, getString(R.string.today_course));

            } else if (viewType == TodayDataRowItem.viewType.VIEW_TYPE_SEANCE.getValue()){
                Seances seance = (Seances) item.data;
                rv = new RemoteViews(packageName, R.layout.widget_today_row_courses);

                DateTime mDateDebut = DateTime.parse(seance.dateDebut);
                DateTime mDateFin = DateTime.parse(seance.dateFin);
                String dateDebutStr = String.format("%d h %02d", mDateDebut.getHourOfDay(),
                        mDateDebut.getMinuteOfHour());
                String dateFinStr = String.format("%d h %02d", mDateFin.getHourOfDay(),
                        mDateFin.getMinuteOfHour());
                rv.setTextViewText(R.id.tv_today_heure_debut, dateDebutStr);
                rv.setTextViewText(R.id.tv_today_heure_fin, dateFinStr);
                rv.setTextViewText(R.id.tv_today_cours_groupe, seance.coursGroupe);
                rv.setTextViewText(R.id.tv_today_nom_activite, seance.nomActivite);
                rv.setTextViewText(R.id.tv_today_libelle_cours, seance.libelleCours);
                rv.setTextViewText(R.id.tv_today_local, seance.local);
            }

            return rv;

        }

        private int getItemViewType(int position) {
            return listeDataRowItems.get(position).type;
        }

        /**
         * This allows for the use of a custom loading view which appears between the time that
         * {@link #getViewAt(int)} is called and returns. If null is returned, a default loading
         * view will be used.
         *
         * @return The RemoteViews representing the desired loading view.
         */
        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        /**
         * See {@link Adapter#getViewTypeCount()}.
         *
         * @return The number of types of Views that will be returned by this factory.
         */
        @Override
        public int getViewTypeCount() {
            return TodayDataRowItem.viewType.values().length;
        }

        /**
         * See {@link Adapter#getItemId(int)}.
         *
         * @param position The position of the item within the data set whose row id we want.
         * @return The id of the item at the specified position.
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * See {@link Adapter#hasStableIds()}.
         *
         * @return True if the same id always refers to the same object.
         */
        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            // Affichage des données locaux actuelles
            databaseHelper = new DatabaseHelper(context);
            updateUI();
        }

        /**
         * Procédure appelée par dataManager à la suite du succès d'une requête
         * @param o
         */
        @Override
        public void onRequestSuccess(Object o) {

            if (o instanceof ListeDeSessions) {

                ListeDeSessions listeDeSessions = (ListeDeSessions) o;
                Date currentDate = new Date();
                Date dateStart;
                Date dateEnd;
                for (int i = listeDeSessions.liste.size() - 1; i > 0; i-- ) {
                    dateStart = Utility.getDateFromString(listeDeSessions.liste.get(i).dateDebut);
                    dateEnd = Utility.getDateFromString(listeDeSessions.liste.get(i).dateFin);
                    if (currentDate.getTime() >= dateStart.getTime() && currentDate.getTime() <= dateEnd.getTime()) {
                        String dateStartString = Utility.getStringForApplETSApiFromDate(dateStart);
                        String dateEndString = Utility.getStringForApplETSApiFromDate(dateEnd);
                        /*
                        Requête permettant de satisfaire la condition syncEventListEnded de horaireManager
                        Sinon, l'observateur n'est jamais avertit.
                         */
                        dataManager.sendRequest(new AppletsApiCalendarRequest(context, dateStartString, dateEndString), this);
                        break;
                    }
                }
            }

            horaireManager.onRequestSuccess(o);
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
            databaseHelper = new DatabaseHelper(context);
            updateUI();
        }
    }
}
