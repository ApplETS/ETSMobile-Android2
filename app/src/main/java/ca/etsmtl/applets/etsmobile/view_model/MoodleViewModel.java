package ca.etsmtl.applets.etsmobile.view_model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleAssignmentCourse;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleAssignmentSubmission;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleCourses;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleProfile;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleRepository;
import ca.etsmtl.applets.etsmobile.model.RemoteResource;

/**
 * Created by Sonphil on 31-08-17.
 */

public class MoodleViewModel extends AndroidViewModel {

    private MoodleRepository repository;
    private LiveData<RemoteResource<MoodleProfile>> profile;
    private LiveData<RemoteResource<List<MoodleAssignmentCourse>>> assignmentCourses;
    private LiveData<RemoteResource<MoodleCourses>> courses;
    private LiveData<RemoteResource<MoodleAssignmentSubmission>> assignmentSubmission;

    public MoodleViewModel(Application application) {
        super(application);

        repository = new MoodleRepository(getApplication());
    }

    public LiveData<RemoteResource<MoodleProfile>> getProfile() {
        if (profile == null) {
            this.profile = repository.getProfile();
        }

        return profile;
    }

    public LiveData<RemoteResource<List<MoodleAssignmentCourse>>> getAssignmentCourses() {
        if (assignmentCourses == null) {
            this.assignmentCourses = repository.getAssignmentCourses();
        }

        return assignmentCourses;
    }

    public LiveData<RemoteResource<MoodleCourses>> getCourses() {
        if (courses == null)
            this.courses = repository.getCourses();

        return courses;
    }

    public LiveData<RemoteResource<MoodleAssignmentSubmission>> getAssignmentSubmission(int assignId) {
        if (assignmentSubmission == null ||assignmentSubmission.getValue() == null
                || assignmentSubmission.getValue().data == null
                || assignmentSubmission.getValue().data.getFeedback() == null
                || assignmentSubmission.getValue().data.getFeedback().getGrade() == null
                || assignmentSubmission.getValue().data.getFeedback().getGrade().getAssignment() != assignId) {
            this.assignmentSubmission = repository.getAssignmentSubmission(assignId);
        }

        return assignmentSubmission;
    }
}
