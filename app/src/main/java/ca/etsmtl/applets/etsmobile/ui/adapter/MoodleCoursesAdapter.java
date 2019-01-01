package ca.etsmtl.applets.etsmobile.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.etsmtl.applets.etsmobile.model.moodle.MoodleCourse;
import ca.etsmtl.applets.etsmobile2.R;


/**
 * Adapter listing all Moodle courses
 *
 * @author Thibaut
 */
public class MoodleCoursesAdapter extends ArrayAdapter<MoodleCourse> {

    private static final int TYPE_COURSE = 0;
    private static final int TYPE_SEPARATOR = 1;

    private LayoutInflater inflater;

    private ArrayList<MoodleCourse> mData = new ArrayList<>();
    private TreeSet<Integer> sectionHeader = new TreeSet<>();

    public MoodleCoursesAdapter(Context context, int rowLayoutResourceId) {
        super(context, rowLayoutResourceId);
        this.inflater = LayoutInflater.from(context);
    }
    public void addCourse(final MoodleCourse course) {
        mData.add(course);
        notifyDataSetChanged();
    }

    public void addSectionHeader(final MoodleCourse semester) {
        mData.add(semester);
        sectionHeader.add(mData.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_COURSE;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public MoodleCourse getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            holder = new ViewHolder();
        }
        switch (getItemViewType(position)) {
                case TYPE_COURSE:
                    view = inflater.inflate(R.layout.row_moodle_course, parent, false);
                    final MoodleCourse item = getItem(position);

                    holder.tvCourseName = (TextView) view.findViewById(R.id.tv_moodle_course_name);
                    holder.tvCourseSigle = (TextView) view.findViewById(R.id.tv_moodle_course_sigle);
                    // Extracts course and group
                    Pattern pattern = Pattern.compile("([A-Z]{3,3}\\d{3,3}(-.[^ ]*)?)");
                    Matcher matcher = pattern.matcher(item.getFullname());
                    if (matcher.find())
                        holder.tvCourseSigle.setText(matcher.group(1));
                    else {
                        holder.tvCourseSigle.setText(item.getFullname());
                        view.setTag(holder);
                        break;
                    }
                    
                    //Extracts course's full name and session
                    pattern = Pattern.compile("(?:[^ ]* )(.*)");
                    matcher = pattern.matcher(item.getFullname());
                    if (matcher.find())
                        holder.tvCourseName.setText(matcher.group(1).replace("(", "{").split("\\{")[0]);
                    else {
                        holder.tvCourseName.setText(item.getFullname());
                    }
                    view.setTag(holder);
                    break;

                case TYPE_SEPARATOR:
                    view = inflater.inflate(R.layout.list_separator_moodle, parent, false);
                    holder.tvCourseSeperator = (TextView) view.findViewById(R.id.textViewSeparator);
                    holder.tvCourseSeperator.setText(getItem(position).getFullname());
                    view.setTag(holder);
                    break;
            }
        return view;
    }

    static class ViewHolder {
        TextView tvCourseSigle;
        TextView tvCourseName;
        TextView tvCourseSeperator;
    }
}
