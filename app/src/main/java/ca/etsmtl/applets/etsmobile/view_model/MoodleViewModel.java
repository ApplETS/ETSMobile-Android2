package ca.etsmtl.applets.etsmobile.view_model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleAssignment;
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

    private static final String TAG = "MoodleViewModel";
    private static final String MOODLE_PREFS = "MoodlePrefs";
    private static final String DISPLAY_PAST_ASSIGNMENTS_PREF = "DisplayPastAssignmentPref";
    public static final int SORT_BY_DATE = 0;
    public static final int SORT_ALPHA = 1;
    private static final String SORT_ASSIGNMENTS_PREF = "SortAssignmentsPref";

    private MoodleRepository repository;
    private Comparator<MoodleAssignment> dateComparator;
    private Comparator<MoodleAssignment> alphaComparator;
    private LiveData<RemoteResource<MoodleProfile>> profile;
    private LiveData<RemoteResource<List<MoodleAssignmentCourse>>> assignmentCourses;
    private LiveData<RemoteResource<MoodleCourses>> courses;
    private LiveData<RemoteResource<MoodleAssignmentSubmission>> assignmentSubmission;
    private MutableLiveData<List<MoodleAssignmentCourse>> filteredCourses = new MutableLiveData<>();
    private MutableLiveData<MoodleAssignment> selectedAssignment = new MutableLiveData<>();

    public MoodleViewModel(Application application, MoodleRepository moodleRepository) {
        super(application);

        Log.d(TAG, "New instance of MoodleViewModel");

        this.repository = moodleRepository;
    }

    public LiveData<RemoteResource<MoodleProfile>> getProfile() {
        if (profile == null || profile.getValue() == null || profile.getValue().data == null) {
            this.profile = repository.getProfile();
        }

        return profile;
    }

    public LiveData<RemoteResource<List<MoodleAssignmentCourse>>> getAssignmentCourses() {
        if (assignmentCourses == null || assignmentCourses.getValue() == null
                || assignmentCourses.getValue().data == null) {
            this.assignmentCourses = repository.getAssignmentCourses();
        }

        return assignmentCourses;
    }

    public LiveData<RemoteResource<MoodleCourses>> getCourses() {
        if (courses == null || courses.getValue() == null || courses.getValue().data == null)
            this.courses = repository.getCourses();

        return courses;
    }

    public LiveData<RemoteResource<MoodleAssignmentSubmission>> getAssignmentSubmission(int assignId) {
        if (assignmentSubmission == null || assignmentSubmission.getValue() == null
                || assignmentSubmission.getValue().data == null
                || assignmentSubmission.getValue().data.getFeedback() == null
                || assignmentSubmission.getValue().data.getFeedback().getGrade() == null
                || assignmentSubmission.getValue().data.getFeedback().getGrade().getAssignment() != assignId) {
            this.assignmentSubmission = repository.getAssignmentSubmission(assignId);
        }

        return assignmentSubmission;
    }

    public void setDisplayPastAssignments(boolean display) {
        SharedPreferences settings = getApplication().getSharedPreferences(MOODLE_PREFS,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(DISPLAY_PAST_ASSIGNMENTS_PREF, display);
        editor.apply();
    }

    public boolean isDisplayPastAssignments() {
        SharedPreferences settings = getApplication().getSharedPreferences(MOODLE_PREFS,
                Context.MODE_PRIVATE);
        return settings.getBoolean(DISPLAY_PAST_ASSIGNMENTS_PREF, false);
    }

    public void setAssignmentsSort(int sort) {
        SharedPreferences settings = getApplication().getSharedPreferences(MOODLE_PREFS,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(SORT_ASSIGNMENTS_PREF, sort);
        editor.apply();
    }

    public int getAssignmentsSortIndex() {
        SharedPreferences settings = getApplication().getSharedPreferences(MOODLE_PREFS,
                Context.MODE_PRIVATE);

        return settings.getInt(SORT_ASSIGNMENTS_PREF, SORT_BY_DATE);
    }

    public Comparator<MoodleAssignment> getAssignmentsSortComparator() {
        int sort = getAssignmentsSortIndex();
        Comparator<MoodleAssignment> currentComparator = null;

        if (sort == SORT_BY_DATE) {
            if (dateComparator == null) {
                dateComparator = new Comparator<MoodleAssignment>() {
                    @Override
                    public int compare(MoodleAssignment a1, MoodleAssignment a2) {
                        return a1.getDueDateObj().compareTo(a2.getDueDateObj());
                    }
                };
            }

            currentComparator = dateComparator;
        } else {
            if (alphaComparator == null) {
                alphaComparator = new Comparator<MoodleAssignment>() {
                    @Override
                    public int compare(MoodleAssignment a1, MoodleAssignment a2) {
                        return a1.getName().compareTo(a2.getName());
                    }
                };
            }

            currentComparator = alphaComparator;
        }

        return currentComparator;
    }

    /**
     * Filtrage des cours en retirant les cours n'ayant aucun devoir
     *
     * @return liste de cours filtrés
     */
    public LiveData<List<MoodleAssignmentCourse>> filterAssignmentCourses() {
        List<MoodleAssignmentCourse> filteredCourses = new ArrayList<>();

        if (assignmentCourses.getValue() != null && this.assignmentCourses.getValue().data != null) {
            List<MoodleAssignmentCourse> assignmentsCourses = this.assignmentCourses.getValue().data;

            for (MoodleAssignmentCourse course : assignmentsCourses) {

                int nbAssignments = course.getAssignments().size();

                if (nbAssignments != 0) {
                /*
                 Si les devoirs antérieurs ne doivent pas être affichés, ceux-ci ne doivent pas être
                 pris en compte.
                 */
                    if (!isDisplayPastAssignments()) {
                        for (MoodleAssignment assignment : course.getAssignments()) {
                            Date dueDate = assignment.getDueDateObj();
                            Date currentDate = new Date();
                            if (dueDate.before(currentDate))
                                nbAssignments--;
                        }
                    }

                    // Ajout du cours si celui-ci contient des devoirs
                    if (nbAssignments != 0)
                        filteredCourses.add(course);
                }
            }

        }

        this.filteredCourses.setValue(filteredCourses);

        return this.filteredCourses;
    }

    public MoodleAssignment selectAssignment(int courseIndex, int assignmentIndex) {
        MoodleAssignment selectedAssignment = null;

        if (filteredCourses.getValue() != null) {
            if (isDisplayPastAssignments()) {
                selectedAssignment = this.filteredCourses.getValue().get(courseIndex).getAssignments().get(assignmentIndex);
            } else {
                int index = 0;
                for (MoodleAssignment assignment : this.filteredCourses.getValue().get(courseIndex).getAssignments()) {
                    Date dueDate = assignment.getDueDateObj();
                    Date currentDate = new Date();
                    if (dueDate.before(currentDate))
                        continue;
                    else if (index == assignmentIndex) {
                        selectedAssignment = assignment;
                        break;
                    } else {
                        index++;
                    }
                }
            }
        }

        this.selectedAssignment.setValue(selectedAssignment);

        return selectedAssignment;
    }

    public MoodleAssignment getSelectedAssignment() {
        return selectedAssignment.getValue();
    }
}
