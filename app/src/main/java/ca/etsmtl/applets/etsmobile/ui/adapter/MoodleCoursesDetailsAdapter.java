package ca.etsmtl.applets.etsmobile.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.octo.android.robospice.request.listener.RequestListener;

import java.util.List;

import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleCoreModule;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by gnut3ll4 on 10/30/14.
 */
public class MoodleCoursesDetailsAdapter extends ArrayAdapter<MoodleCoreModule> {

    private LayoutInflater inflater;
    private RequestListener<Object> listener;

    public MoodleCoursesDetailsAdapter(Context context, int rowLayoutResourceId, List<MoodleCoreModule> listMoodleCoreModules, RequestListener<Object> listener) {
        super(context, rowLayoutResourceId, listMoodleCoreModules);
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.row_moodle_course_detail, parent, false);
            holder = new ViewHolder();
            holder.tvCourseDetail = (TextView) view.findViewById(R.id.tv_moodle_course_detail);


            view.setTag(holder);
        }

        final MoodleCoreModule item = getItem(position);

//        holder.tvCourseDetail.setText(item.getShortname());
        holder.tvCourseDetail.setText(item.getName());




        return view;
    }

    static class ViewHolder {
        TextView tvCourseDetail;

    }

}
