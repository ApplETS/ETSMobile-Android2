package ca.etsmtl.applets.etsmobile.ui.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.octo.android.robospice.request.listener.RequestListener;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleCourse;
import ca.etsmtl.applets.etsmobile.ui.fragment.MoodleCourseDetailsFragment;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by gnut3ll4 on 10/19/14.
 */
public class MoodleCoursesAdapter extends ArrayAdapter<MoodleCourse> {

    private LayoutInflater inflater;
    private RequestListener<Object> listener;

    public MoodleCoursesAdapter(Context context, int rowLayoutResourceId, List<MoodleCourse> listMoodleCourse, RequestListener<Object> listener) {
        super(context, rowLayoutResourceId, listMoodleCourse);
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
            view = inflater.inflate(R.layout.row_moodle_course, parent, false);
            holder = new ViewHolder();
            holder.tvCourseName = (TextView) view.findViewById(R.id.tv_moodle_course_name);
            holder.tvCourseSigle = (TextView) view.findViewById(R.id.tv_moodle_course_sigle);

            view.setTag(holder);
        }

        final MoodleCourse item = getItem(position);


        Pattern pattern = Pattern.compile("(?:[^-]*-)(.*)");
//        Pattern pattern = Pattern.compile("([A-Z]{3,3}\\d{3,3}[-0-9]{0,3}) ([^\\(]*)(?:\\()?([AÉH]\\d{4,4})?");

        Matcher matcher = pattern.matcher(item.getShortname());
        if(matcher.find())
            holder.tvCourseSigle.setText(matcher.group(1));

        pattern = Pattern.compile("(?:[^ ]* )(.*)");
        matcher = pattern.matcher(item.getFullname());
        if(matcher.find())
            holder.tvCourseName.setText(matcher.group(1));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = MoodleCourseDetailsFragment.newInstance(item.getId());
                FragmentManager fragmentManager = ((Activity) inflater.getContext()).getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "MoodleCourseDetailsFragment")
                        .addToBackStack(null).commit();

            }
        });


        return view;
    }



    static class ViewHolder {
        TextView tvCourseSigle;
        TextView tvCourseName;

    }

}
