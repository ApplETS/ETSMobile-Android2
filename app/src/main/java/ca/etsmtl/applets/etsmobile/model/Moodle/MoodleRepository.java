package ca.etsmtl.applets.etsmobile.model.Moodle;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.List;

import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.http.MoodleAssignmentCoursesRequest;
import ca.etsmtl.applets.etsmobile.model.RemoteResource;

/**
 * Created by Sonphil on 31-08-17.
 */

public class MoodleRepository {

    private Context context;
    private DataManager dataManager;
    private MutableLiveData<RemoteResource<List<MoodleAssignmentCourse>>> assignmentCourses = new MutableLiveData<>();

    public MoodleRepository(Context context) {
        this.context = context;
        dataManager = DataManager.getInstance(context);
    }

    public LiveData<RemoteResource<List<MoodleAssignmentCourse>>> getAssignmentCourses(int[] coursesIds) {

        assignmentCourses.setValue(RemoteResource.<List<MoodleAssignmentCourse>>loading(null));

        dataManager.sendRequest(new MoodleAssignmentCoursesRequest(context, coursesIds), new RequestListener<Object>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                assignmentCourses.setValue(RemoteResource.<List<MoodleAssignmentCourse>>error(spiceException.getLocalizedMessage(), null));
            }

            @Override
            public void onRequestSuccess(Object o) {
                List<MoodleAssignmentCourse> courses = ((MoodleAssignmentCourses) o).getCourses();
                assignmentCourses.setValue(RemoteResource.success(courses));
            }
        });

        return assignmentCourses;
    }
}
