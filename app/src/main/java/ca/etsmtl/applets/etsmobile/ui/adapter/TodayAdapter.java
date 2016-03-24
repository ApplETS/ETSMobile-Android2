package ca.etsmtl.applets.etsmobile.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.ArrayList;

import ca.etsmtl.applets.etsmobile.model.Event;
import ca.etsmtl.applets.etsmobile.model.Seances;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by laurencedevillers on 14-10-22.
 */
public class TodayAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<TodayDataRowItem> arrayDataRowItem;

    public TodayAdapter(Context context, ArrayList<TodayDataRowItem> arrayAdapter) {
        this.context = context;
        this.arrayDataRowItem = arrayAdapter;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public int getCount() {
        return arrayDataRowItem.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayDataRowItem.get(position).data;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return arrayDataRowItem.get(position).type;
    }

    @Override
    public int getViewTypeCount() {
        return TodayDataRowItem.viewType.values().length;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int viewType = getItemViewType(position);
        Object data = getItem(position);


        if (convertView == null) {

            if (viewType == TodayDataRowItem.viewType.VIEW_TYPE_TITLE_EVENT.getValue()) {
                convertView = LayoutInflater.from(context).inflate(R.layout.row_today_title, parent, false);
                TextView textView = (TextView) convertView.findViewById(R.id.todays_title);
                textView.setText(context.getText(R.string.today_event));

            } else if (viewType == TodayDataRowItem.viewType.VIEW_TYPE_EVENT.getValue()) {
                convertView = LayoutInflater.from(context).inflate(R.layout.row_event, parent, false);
                ViewEventHolder eventHolder = new ViewEventHolder();
                eventHolder.tvEvent = (TextView) convertView.findViewById(R.id.event_text);
                convertView.setTag(eventHolder);
            } else if (viewType == TodayDataRowItem.viewType.VIEW_TYPE_TITLE_SEANCE.getValue()) {
                convertView = LayoutInflater.from(context).inflate(R.layout.row_today_title, parent, false);
                TextView textView = (TextView) convertView.findViewById(R.id.todays_title);
                textView.setText(context.getText(R.string.today_course));

            } else if (viewType == TodayDataRowItem.viewType.VIEW_TYPE_SEANCE.getValue()) {
                convertView = LayoutInflater.from(context).inflate(R.layout.row_today_courses, parent, false);
                ViewSeancesHolder seancesHolder = new ViewSeancesHolder();
                seancesHolder.tvHeureDebut = (TextView) convertView.findViewById(R.id.tv_today_heure_debut);
                seancesHolder.tvHeureFin = (TextView) convertView.findViewById(R.id.tv_today_heure_fin);
                seancesHolder.tvCoursGroupe = (TextView) convertView.findViewById(R.id.tv_today_cours_groupe);
                seancesHolder.tvNomActivite = (TextView) convertView.findViewById(R.id.tv_today_nom_activite);
                seancesHolder.tvLibelleCours = (TextView) convertView.findViewById(R.id.tv_today_libelle_cours);
                seancesHolder.tvLocal = (TextView) convertView.findViewById(R.id.tv_today_local);
                convertView.setTag(seancesHolder);
            } else if (viewType == TodayDataRowItem.viewType.VIEW_TYPE_ETS_EVENT.getValue()) {
                convertView = LayoutInflater.from(context).inflate(R.layout.row_today_ets_event, parent, false);
                ViewEtsEventHolder etsEventHolder = new ViewEtsEventHolder();
                etsEventHolder.tvLibelleEvenementETS = (TextView) convertView.findViewById(R.id.tv_today_libelle_ets_evenement);
                convertView.setTag(etsEventHolder);
            }
        }

        if (viewType == TodayDataRowItem.viewType.VIEW_TYPE_EVENT.getValue()) {
            Event event = (Event) data;
            ViewEventHolder viewEventHolder = (ViewEventHolder) convertView.getTag();
            viewEventHolder.tvEvent.setText(event.getTitle());

        } else if (viewType == TodayDataRowItem.viewType.VIEW_TYPE_SEANCE.getValue()) {
            Seances seance = (Seances) data;
            ViewSeancesHolder viewSeancesHolder = (ViewSeancesHolder) convertView.getTag();
            viewSeancesHolder.tvNomActivite.setText(seance.nomActivite);
            viewSeancesHolder.tvCoursGroupe.setText(seance.coursGroupe);
            viewSeancesHolder.tvLibelleCours.setText(seance.libelleCours);
            viewSeancesHolder.tvLocal.setText(seance.local);

            DateTime mDateDebut = DateTime.parse(seance.dateDebut);
            DateTime mDateFin = DateTime.parse(seance.dateFin);

            String dateDebut = String.format("%dh%02d", mDateDebut.getHourOfDay(), mDateDebut.getMinuteOfHour());
            String dateFin = String.format("%dh%02d", mDateFin.getHourOfDay(), mDateFin.getMinuteOfHour());

            viewSeancesHolder.tvHeureDebut.setText(dateDebut);
            viewSeancesHolder.tvHeureFin.setText(dateFin);
        }
        return convertView;
    }


    class ViewEventHolder {
        TextView tvEvent;
    }

    class ViewSeancesHolder {
        TextView tvHeureDebut;
        TextView tvHeureFin;
        TextView tvNomActivite;
        TextView tvCoursGroupe;
        TextView tvLibelleCours;
        TextView tvLocal;
    }

    class ViewEtsEventHolder {
        TextView tvLibelleEvenementETS;
    }
}
