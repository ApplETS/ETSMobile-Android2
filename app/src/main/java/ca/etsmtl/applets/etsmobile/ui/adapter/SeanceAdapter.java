package ca.etsmtl.applets.etsmobile.ui.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ca.etsmtl.applets.etsmobile.model.Seances;
import ca.etsmtl.applets.etsmobile.util.SeanceComparator;
import ca.etsmtl.applets.etsmobile2.R;

public class SeanceAdapter extends BaseAdapter {

    private List<TodayDataRowItem> listSeances;
    private HashMap<String,Integer> colors;
    private int indexColor = 0;
    private int[] rainbow;

    private Context context;

    public SeanceAdapter(Context context) {
        this.context = context;
        listSeances = new ArrayList<>();
        colors = new HashMap<>();
        rainbow = context.getResources().getIntArray(R.array.rainbow);
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
    @Override
    public int getCount() {
        return listSeances.size();
    }

    @Override
    public Object getItem(int position) {
        return listSeances.get(position).data;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return listSeances.get(position).type;
    }

    @Override
    public int getViewTypeCount() {
        return TodayDataRowItem.viewType.values().length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);

        if (convertView == null) {

            if (viewType == TodayDataRowItem.viewType.VIEW_TYPE_TITLE_SEANCE.getValue()) {
                convertView = LayoutInflater.from(context).inflate(R.layout.row_today_title, parent,false);
                ViewSeacesTitleHolder titleHolder = new ViewSeacesTitleHolder();
                titleHolder.tvTitle = (TextView) convertView.findViewById(R.id.todays_title);
                convertView.setTag(titleHolder);

            } else if (viewType == TodayDataRowItem.viewType.VIEW_TYPE_SEANCE.getValue()) {
                convertView = LayoutInflater.from(context).inflate(R.layout.row_today_courses, parent, false );
                ViewSeancesHolder seancesHolder = new ViewSeancesHolder();
                seancesHolder.tvHeureDebut = (TextView) convertView.findViewById(R.id.tv_today_heure_debut);
                seancesHolder.tvHeureFin = (TextView) convertView.findViewById(R.id.tv_today_heure_fin);
                seancesHolder.tvCoursGroupe = (TextView) convertView.findViewById(R.id.tv_today_cours_groupe);
                seancesHolder.tvNomActivite = (TextView) convertView.findViewById(R.id.tv_today_nom_activite);
                seancesHolder.tvLibelleCours = (TextView) convertView.findViewById(R.id.tv_today_libelle_cours);
                seancesHolder.tvLocal = (TextView) convertView.findViewById(R.id.tv_today_local);
                seancesHolder.tvSeparator = (TextView) convertView.findViewById(R.id.tv_vertical_separator);
                convertView.setTag(seancesHolder);
            }
        }

        if (viewType == TodayDataRowItem.viewType.VIEW_TYPE_SEANCE.getValue()) {
            Seances seance = (Seances) getItem(position);
            ViewSeancesHolder viewSeancesHolder = (ViewSeancesHolder) convertView.getTag();
            viewSeancesHolder.tvNomActivite.setText(seance.nomActivite);
            viewSeancesHolder.tvLibelleCours.setText(seance.libelleCours);
            viewSeancesHolder.tvCoursGroupe.setText(seance.coursGroupe);
            viewSeancesHolder.tvLocal.setText(seance.local);

            if(colors.containsKey(seance.nomActivite)) {
                viewSeancesHolder.tvSeparator.setBackgroundColor(colors.get(seance.nomActivite));
            } else {

                colors.put(seance.nomActivite,rainbow[indexColor%rainbow.length]);
                viewSeancesHolder.tvSeparator.setBackgroundColor(rainbow[indexColor%rainbow.length]);
                indexColor++;

            }

            DateTime mDateDebut = DateTime.parse(seance.dateDebut);
            DateTime mDateFin = DateTime.parse(seance.dateFin);

            String dateDebut = String.format("%dh%02d", mDateDebut.getHourOfDay(), mDateDebut.getMinuteOfHour());
            String dateFin = String.format("%dh%02d", mDateFin.getHourOfDay(), mDateFin.getMinuteOfHour());

            viewSeancesHolder.tvHeureDebut.setText(dateDebut);
            viewSeancesHolder.tvHeureFin.setText(dateFin);
        } else if (viewType == TodayDataRowItem.viewType.VIEW_TYPE_TITLE_SEANCE.getValue()) {
            ViewSeacesTitleHolder titleHolder = (ViewSeacesTitleHolder) convertView.getTag();
            titleHolder.tvTitle.setText((String) getItem(position));
        }
        return convertView;
    }

    public List<TodayDataRowItem> getItemList() {
        return listSeances;
    }

    public void setItemList(List<Seances> itemList) {

        listSeances = new ArrayList<>();
        String tempDate = "";
        DateTime today = new DateTime();

        Collections.sort(itemList,new SeanceComparator());

        for(Seances seances : itemList) {

            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
            DateTime seanceDay = formatter.parseDateTime(seances.dateDebut.substring(0,10));

            if(today.isAfter(seanceDay) && !DateUtils.isToday(seanceDay.getMillis()) ) {
                continue;
            }

            if(!seances.dateDebut.substring(0,10).equals(tempDate)) {

                tempDate = seances.dateDebut.substring(0,10);

                DateTime.Property pDoW = seanceDay.dayOfWeek();
                DateTime.Property pDoM = seanceDay.dayOfMonth();
                DateTime.Property pMoY = seanceDay.monthOfYear();

                this.listSeances.add(new TodayDataRowItem(TodayDataRowItem.viewType.VIEW_TYPE_TITLE_SEANCE, context.getString(R.string.date_text, pDoW.getAsText(Locale.getDefault()), pDoM.get(), pMoY.getAsText(Locale.getDefault()))));
            }
            this.listSeances.add(new TodayDataRowItem(TodayDataRowItem.viewType.VIEW_TYPE_SEANCE, seances));
        }

    }

    static class ViewSeancesHolder {
        TextView tvHeureDebut;
        TextView tvHeureFin;
        TextView tvNomActivite;
        TextView tvCoursGroupe;
        TextView tvLibelleCours;
        TextView tvLocal;
        TextView tvSeparator;
    }

    static class ViewSeacesTitleHolder {
        TextView tvTitle;
    }
}
