package ca.etsmtl.applets.etsmobile.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.List;

import ca.etsmtl.applets.etsmobile.model.Seances;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by gnut3ll4 on 9/29/14.
 */
public class TodaySeancesAdapter extends ArrayAdapter<Seances> {


    private LayoutInflater inflater;

    public TodaySeancesAdapter(Context context, int rowResourceId, List<Seances> listSeances) {
        super(context, rowResourceId, listSeances);
        this.inflater = LayoutInflater.from(context);
    }


    @SuppressLint("DefaultLocale")
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.row_today_courses, parent, false);
            holder = new ViewHolder();
            holder.tvHeureDebut = (TextView) view.findViewById(R.id.tv_today_heure_debut);
            holder.tvHeureFin = (TextView) view.findViewById(R.id.tv_today_heure_fin);
            holder.tvCoursGroupe = (TextView) view.findViewById(R.id.tv_today_cours_groupe);
            holder.tvNomActivite = (TextView) view.findViewById(R.id.tv_today_nom_activite);
            holder.tvLocal = (TextView) view.findViewById(R.id.tv_today_local);

            view.setTag(holder);
        }

        Seances item = getItem(position);

        holder.tvNomActivite.setText(item.nomActivite);
        holder.tvCoursGroupe.setText(item.coursGroupe);
        holder.tvLocal.setText(item.local);

        DateTime mDateDebut = DateTime.parse(item.dateDebut);
        DateTime mDateFin = DateTime.parse(item.dateFin);

        String dateDebut = String.format("%dh%02d",mDateDebut.getHourOfDay(), mDateDebut.getMinuteOfHour());
        String dateFin = String.format("%dh%02d",mDateFin.getHourOfDay(), mDateFin.getMinuteOfHour());

        holder.tvHeureDebut.setText(dateDebut);
        holder.tvHeureFin.setText(dateFin);

        return view;
    }

    static class ViewHolder {
        TextView tvHeureDebut;
        TextView tvHeureFin;
        TextView tvNomActivite;
        TextView tvCoursGroupe;
        TextView tvLocal;
    }
}
