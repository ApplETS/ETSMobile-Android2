package ca.etsmtl.applets.etsmobile.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.joda.time.DateTime;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import ca.etsmtl.applets.etsmobile.db.DatabaseHelper;
import ca.etsmtl.applets.etsmobile.model.Event;
import ca.etsmtl.applets.etsmobile.model.Seances;
import ca.etsmtl.applets.etsmobile.ui.adapter.TodayDataRowItem;
import ca.etsmtl.applets.etsmobile.util.SeanceComparator;
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

    private class ListRemoteViewFactory implements RemoteViewsFactory {

        private List<Seances> listeSeances;
        private List<Event> listeEvents;
        private List<TodayDataRowItem> dataRowItems;
        private Context context;
        private int appWidgetId;
        private DatabaseHelper databaseHelper;

        public ListRemoteViewFactory(Context context, Intent intent) {
            this.context = context;
            this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            dataRowItems = new ArrayList<TodayDataRowItem>();
        }

        /**
         * Called when your factory is first constructed. The same factory may be shared across
         * multiple RemoteViewAdapters depending on the intent passed.
         */
        @Override
        public void onCreate() {
            databaseHelper = new DatabaseHelper(context);
            try {
                DateTime dateTime = new DateTime();
                SimpleDateFormat seancesFormatter = new SimpleDateFormat("yyyy-MM-dd", context.getResources().getConfiguration().locale);
                listeSeances = databaseHelper.getDao(Seances.class).queryBuilder().where().like("dateDebut", seancesFormatter.format(dateTime.toDate()).toString() + "%").query();
                Collections.sort(listeSeances, new SeanceComparator());
                listeEvents = databaseHelper.getDao(Event.class).queryBuilder().where().like("startDate", seancesFormatter.format(dateTime.toDate()).toString() + "%").query();
            } catch (SQLException e) {
                e.printStackTrace();
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
            return listeSeances.size();
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

            Seances seance = listeSeances.get(position);
            /*RemoteViews rv = new RemoteViews(context.getPackageName(),
                    R.layout.seances_item);
            // TODO Regarder TodayApdapter
            rv.setTextViewText(R.id.seanceItemNomCoursTv, seance.libelleCours);
            rv.setTextViewText(R.id.seanceItemTypeTv, seance.nomActivite);
            DateTime mDateDebut = DateTime.parse(seance.dateDebut);
            DateTime mDateFin = DateTime.parse(seance.dateFin);
            String dateDebutStr = String.format("%d h %02d", mDateDebut.getHourOfDay(), mDateDebut.getMinuteOfHour());
            String dateFinStr = String.format("%d h %02d", mDateFin.getHourOfDay(), mDateFin.getMinuteOfHour());
            rv.setTextViewText(R.id.seanceItemDureeTv, dateDebutStr);
            rv.setTextViewText(R.id.seanceItemLocalTv, seance.local);*/

            RemoteViews rv = new RemoteViews(context.getPackageName(),
                    R.layout.widget_row_today_courses);

            DateTime mDateDebut = DateTime.parse(seance.dateDebut);
            DateTime mDateFin = DateTime.parse(seance.dateFin);
            String dateDebutStr = String.format("%d h %02d", mDateDebut.getHourOfDay(), mDateDebut.getMinuteOfHour());
            String dateFinStr = String.format("%d h %02d", mDateFin.getHourOfDay(), mDateFin.getMinuteOfHour());
            rv.setTextViewText(R.id.tv_today_heure_debut, dateDebutStr);
            rv.setTextViewText(R.id.tv_today_heure_fin, dateFinStr);
            rv.setTextViewText(R.id.tv_today_cours_groupe, seance.coursGroupe);
            rv.setTextViewText(R.id.tv_today_nom_activite, seance.nomActivite);
            rv.setTextViewText(R.id.tv_today_libelle_cours, seance.libelleCours);
            rv.setTextViewText(R.id.tv_today_local, seance.local);

            return rv;

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
            return 1;
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
            return true;
        }
    }
}
