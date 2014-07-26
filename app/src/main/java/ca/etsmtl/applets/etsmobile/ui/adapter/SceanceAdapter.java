package ca.etsmtl.applets.etsmobile.ui.adapter;

import java.util.ArrayList;

import org.joda.time.DateTime;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import ca.etsmtl.applets.etsmobile.model.TodaysCourses.Seance;
import ca.etsmtl.applets.etsmobile2.R;

public class SceanceAdapter extends ArrayAdapter<Seance> {

    private LayoutInflater inflater;

    public SceanceAdapter(Context context, ArrayList<Seance> list) {
        super(context, R.layout.list_item_value, list);
        this.inflater = LayoutInflater.from(context);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.seances_item, parent, false);
            holder = new ViewHolder();
            holder.tvName = (TextView) view.findViewById(R.id.seanceItemNomCoursTv);
            holder.tvType = (TextView) view.findViewById(R.id.seanceItemTypeTv);
            holder.tvLocal = (TextView) view.findViewById(R.id.seanceItemLocalTv);
            holder.tvDuree = (TextView) view.findViewById(R.id.seanceItemDureeTv);

            view.setTag(holder);
        }

        Seance item = getItem(position);
        holder.tvName.setText(item.libelleCours);
        holder.tvType.setText(item.nomActivite);
        holder.tvLocal.setText(item.local);

        String timeLabel = "";

        DateTime mDateDebut = DateTime.parse(item.dateDebut);
        DateTime mDateFin = DateTime.parse(item.dateFin);

        timeLabel = String.format("%dh%02d - %dh%02d", mDateDebut.getHourOfDay(), mDateDebut.getMinuteOfHour(),
                mDateFin.getHourOfDay(), mDateFin.getMinuteOfHour());

        holder.tvDuree.setText(timeLabel);

        return view;
    }

    static class ViewHolder {
        TextView tvName;
        TextView tvType;
        TextView tvLocal;
        TextView tvDuree;
    }
}
