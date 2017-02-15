package ca.etsmtl.applets.etsmobile;


import android.content.Context;
import android.support.wearable.view.GridPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Locale;

import ca.etsmtl.applets.etsmobile.view.ProgressLayout;

public class SeancesPagerAdapter extends GridPagerAdapter {
    final Context mContext;
    private final LayoutInflater inflater;
    List<Seances> mSeances;

    public SeancesPagerAdapter(final Context context, List<Seances> seancesList) {
        mContext = context;
        this.mSeances = seancesList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getColumnCount(int arg0) {
        return mSeances.size();
    }

    @Override
    public int getRowCount() {
        return mSeances.size() > 0 ? 1 : 0;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int row, int col) {
        final View view = inflater.inflate(R.layout.item_seance_gridviewpager, container, false);

        final TextView tvDate = (TextView) view.findViewById(R.id.tv_date);
        final TextView tvStartHour = (TextView) view.findViewById(R.id.tv_start_hour);
        final TextView tvStartMinute = (TextView) view.findViewById(R.id.tv_start_minute);
        final TextView tvEndHour = (TextView) view.findViewById(R.id.tv_end_hour);
        final TextView tvEndMinute = (TextView) view.findViewById(R.id.tv_end_minute);
        final TextView tvActivityName = (TextView) view.findViewById(R.id.tv_activity_name);
        final TextView tvCourseGroupId = (TextView) view.findViewById(R.id.tv_course_group_id);
        final TextView tvLocal = (TextView) view.findViewById(R.id.tv_local);
        final ProgressLayout progressLayout = (ProgressLayout) view.findViewById(R.id.progressLayout);

        Seances seance = mSeances.get(col);

        DateTime startDateTime = DateTime.parse(seance.dateDebut);
        DateTime endDateTime = DateTime.parse(seance.dateFin);

        String startHour = String.format("%d", startDateTime.getHourOfDay());
        String startMinute = String.format("%02d", startDateTime.getMinuteOfHour());
        String endHour = String.format("%d", endDateTime.getHourOfDay());
        String endMinute = String.format("%02d", endDateTime.getMinuteOfHour());

        DateTime dateTime = new DateTime();

        //currently during the course
        if (dateTime.isBefore(endDateTime) && dateTime.isAfter(startDateTime)) {
            progressLayout.setBoundsCourse(startDateTime, endDateTime);
            progressLayout.setAutoProgress(true);
            progressLayout.start();
        } else {
            progressLayout.setCurrentProgress(100);
        }


        DateTime.Property pDoW = dateTime.dayOfWeek();
        DateTime.Property pDoM = dateTime.dayOfMonth();
        DateTime.Property pMoY = dateTime.monthOfYear();

        Locale currentLocale = mContext.getResources().getConfiguration().locale;

        tvDate.setText(
                mContext.getString(
                        R.string.horaire, pDoW.getAsText(currentLocale),
                        "" + pDoM.get(),
                        pMoY.getAsText(currentLocale)));


        tvStartHour.setText(startHour);
        tvStartMinute.setText(startMinute);
        tvEndHour.setText(endHour);
        tvEndMinute.setText(endMinute);
        tvActivityName.setText(seance.nomActivite);
        tvCourseGroupId.setText(seance.coursGroupe);
        tvLocal.setText(seance.local);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int row, int col, Object view) {
        container.removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setSeances(List<Seances> seances) {
        mSeances = seances;
        notifyDataSetChanged();
    }
}
