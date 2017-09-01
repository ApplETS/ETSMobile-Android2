package ca.etsmtl.applets.etsmobile.view_model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleAssignmentCourse;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleRepository;
import ca.etsmtl.applets.etsmobile.model.RemoteResource;

/**
 * Created by Sonphil on 31-08-17.
 */

public class MoodleViewModel extends AndroidViewModel {

    private MoodleRepository repository;
    private LiveData<RemoteResource<List<MoodleAssignmentCourse>>> assignmentCourses;

    public MoodleViewModel(Application application) {
        super(application);

        repository = new MoodleRepository(getApplication());
    }

    public LiveData<RemoteResource<List<MoodleAssignmentCourse>>> getAssignmentCourses(int[] coursesIds) {
        if (assignmentCourses == null) {
            this.assignmentCourses = repository.getAssignmentCourses(coursesIds);
        }

        return assignmentCourses;
    }
}
