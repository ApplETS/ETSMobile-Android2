package ca.etsmtl.applets.etsmobile;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Created by steven on 16-02-20.
 */
public class TodayAdapter extends ArrayAdapter<Seances> {

    private Context context;
    private ArrayList<Seances> seancesList;
    private LayoutInflater inflater;

    public TodayAdapter(Context context, int rowLayoutResourceId, ArrayList<Seances> list) {
        super(context, rowLayoutResourceId, list);
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        }else{
            view = inflater.inflate(R.layout.row_today_courses, parent, false);
            holder = new ViewHolder();
            holder.tvHeureDebut = (TextView) view.findViewById(R.id.tv_today_heure_debut);
            holder.tvHeureFin = (TextView) view.findViewById(R.id.tv_today_heure_fin);
            holder.tvNomActivite = (TextView) view.findViewById(R.id.tv_today_nom_activite);
            holder.tvCoursGroupe = (TextView) view.findViewById(R.id.tv_today_cours_groupe);
            holder.tvLibelleCours = (TextView) view.findViewById(R.id.tv_today_libelle_cours);
            holder.tvLocal = (TextView) view.findViewById(R.id.tv_today_local);
            view.setTag(holder);
        }
        Seances item = getItem(position);

        DateTime mDateDebut = DateTime.parse(item.dateDebut);
        DateTime mDateFin = DateTime.parse(item.dateFin);

        String dateDebut = String.format("%dh%02d", mDateDebut.getHourOfDay(), mDateDebut.getMinuteOfHour());
        String dateFin = String.format("%dh%02d", mDateFin.getHourOfDay(), mDateFin.getMinuteOfHour());

        holder.tvHeureDebut.setText(dateDebut);
        holder.tvHeureFin.setText(dateFin);
        holder.tvNomActivite.setText(item.nomActivite);
        holder.tvCoursGroupe.setText(item.coursGroupe);
        holder.tvLibelleCours.setText(item.libelleCours);
        holder.tvLocal.setText(item.local);

        return view;
    }

    class ViewHolder {
        TextView tvHeureDebut;
        TextView tvHeureFin;
        TextView tvNomActivite;
        TextView tvCoursGroupe;
        TextView tvLibelleCours;
        TextView tvLocal;
    }
}
