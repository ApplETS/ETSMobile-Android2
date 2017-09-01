package ca.etsmtl.applets.etsmobile.http;

import android.content.Context;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleAssignmentCourses;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by Sonphil on 31-08-17.
 */

public class MoodleAssignmentCoursesRequest extends SpringAndroidSpiceRequest<MoodleAssignmentCourses> {

    private Context context;
    private int[] coursesIds;
    private String moodleToken;

    public MoodleAssignmentCoursesRequest(Context context, int[] courseIds, String moodleToken) {
        super(MoodleAssignmentCourses.class);

        this.context = context;
        this.coursesIds = courseIds;
        this.moodleToken = moodleToken;
    }

    @Override
    public MoodleAssignmentCourses loadDataFromNetwork() throws Exception {
        String coursesIdsStr = "";

        for (int id : coursesIds) {
            coursesIdsStr += "&courseids[]=" + id;
        }

        String url = context.getString(R.string.moodle_api_assignments, moodleToken, coursesIdsStr);

        return getRestTemplate().getForObject(url, MoodleAssignmentCourses.class);
    }
}
