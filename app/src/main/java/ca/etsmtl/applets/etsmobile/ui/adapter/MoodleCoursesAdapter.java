package ca.etsmtl.applets.etsmobile.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import java.util.List;

import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleCoreCourses;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleCourse;
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

        holder.tvCourseSigle.setText(item.getShortname());
        holder.tvCourseName.setText(item.getFullname());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(inflater.getContext(), "" + item.getId(), Toast.LENGTH_SHORT).show();
                queryMoodleCoreCourses(item);
            }
        });


        return view;
    }

    private void queryMoodleCoreCourses(final MoodleCourse moodleCourse) {
        SpringAndroidSpiceRequest<Object> request = new SpringAndroidSpiceRequest<Object>(null) {

            @Override
            public MoodleCoreCourses loadDataFromNetwork() throws Exception {
                String url = inflater.getContext().getString(R.string.moodle_api_core_course_get_contents, moodleCourse.token,moodleCourse.getId());

                return getRestTemplate().getForObject(url, MoodleCoreCourses.class);
            }
        };

        DataManager dataManager = DataManager.getInstance(inflater.getContext());

        dataManager.sendRequest(request, listener);
    }

    static class ViewHolder {
        TextView tvCourseSigle;
        TextView tvCourseName;

    }

}
