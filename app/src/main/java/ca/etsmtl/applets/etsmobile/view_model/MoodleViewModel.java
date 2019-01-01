package ca.etsmtl.applets.etsmobile.view_model;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ca.etsmtl.applets.etsmobile.model.moodle.MoodleAssignment;
import ca.etsmtl.applets.etsmobile.model.moodle.MoodleAssignmentCourse;
import ca.etsmtl.applets.etsmobile.model.moodle.MoodleAssignmentSubmission;
import ca.etsmtl.applets.etsmobile.model.moodle.MoodleCourse;
import ca.etsmtl.applets.etsmobile.model.moodle.MoodleProfile;
import ca.etsmtl.applets.etsmobile.model.moodle.MoodleToken;
import ca.etsmtl.applets.etsmobile.model.RemoteResource;
import ca.etsmtl.applets.etsmobile.repository.MoodleRepository;

/**
 * Created by Sonphil on 31-08-17.
 */

public class MoodleViewModel extends AndroidViewModel {

    private static final String TAG = "MoodleViewModel";
    private static final String MOODLE_PREFS = "MoodlePrefs";
    private static final String DISPLAY_PAST_ASSIGNMENTS_PREF = "DisplayPastAssignmentPref";
    private static final String DISPLAY_SHOW_CASE_PREF = "DisplayShowCase";
    /**
     * Index of the sort by date option
     **/
    public static final int SORT_BY_DATE = 0;
    /**
     * Index of the sort alphabetically option
     **/
    public static final int SORT_ALPHA = 1;
    private static final String SORT_ASSIGNMENTS_PREF = "SortAssignmentsPref";
    private static final String SORT_ORDER_ASSIGNMENTS_PREF = "SortOrderAssignmentsPref";
    /**
     * From low to high
     */
    private static final int SORT_ASC = 1;
    /**
     * From high to low
     */
    private static final int SORT_DESC = -1;

    private MoodleRepository repository;
    private LiveData<RemoteResource<MoodleToken>> token;
    private LiveData<RemoteResource<MoodleProfile>> profile;
    private MutableLiveData<RemoteResource<List<MoodleAssignmentCourse>>> assignmentCourses = new MutableLiveData<>();
    private MediatorLiveData<RemoteResource<List<MoodleAssignmentCourse>>> filteredAssignmentCourses;
    private LiveData<RemoteResource<List<MoodleCourse>>> courses;
    private LiveData<RemoteResource<MoodleAssignmentSubmission>> assignmentSubmission = new MutableLiveData<>();
    private MutableLiveData<MoodleAssignment> selectedAssignment = new MutableLiveData<>();

    public MoodleViewModel(Application application, MoodleRepository moodleRepository) {
        super(application);

        this.repository = moodleRepository;
    }

    /**
     * Returns true if the Moodle Assignment screen's showcase has been displayed
     * <p>
     * The boolean value is obtained from the preferences.
     *
     * @return true if the Moodle Assignment screen's showcase has been displayed
     */
    public boolean isShowCaseHasBeenDisplayed() {
        SharedPreferences settings = getApplication().getSharedPreferences(MOODLE_PREFS,
                Context.MODE_PRIVATE);
        return settings.getBoolean(DISPLAY_SHOW_CASE_PREF, false);
    }

    /**
     * Set a value indicating whether Moodle Assignment screen's showcase has been displayed or not
     * <p>
     * The boolean value is set in the preferences.
     *
     * @param display true if the Moodle Assignment screen's showcase has been displayed
     */
    public void setShowCaseHasBeenDisplayed(boolean display) {
        SharedPreferences settings = getApplication().getSharedPreferences(MOODLE_PREFS,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(DISPLAY_SHOW_CASE_PREF, display);
        editor.apply();
    }


    /**
     * Returns the courses
     *
     * @return {@link LiveData} instance which contains the courses
     */
    public LiveData<RemoteResource<List<MoodleCourse>>> getCourses() {
        if (courses == null || courses.getValue() == null || courses.getValue().data == null) {
            courses = repository.getCourses();
        }

        return courses;
    }

    /**
     * Returns the assignments courses
     * <p>
     * Each course contains a list of Moodle assignments that the user can view for that course
     *
     * @return {@link LiveData} instance which contains the assignments courses
     */
    public LiveData<RemoteResource<List<MoodleAssignmentCourse>>> getAssignmentCourses() {
        if (filteredAssignmentCourses == null || filteredAssignmentCourses.getValue() == null
                || filteredAssignmentCourses.getValue().data == null) {
            filteredAssignmentCourses = new MediatorLiveData<>();
            filteredAssignmentCourses.addSource(repository.getAssignmentCourses(), assignmentCoursesRes -> {
                assignmentCourses.setValue(assignmentCoursesRes);
                if (assignmentCoursesRes != null && assignmentCoursesRes.data != null) {
                    setFilteredAssignmentCourses(assignmentCoursesRes);
                } else {
                    filteredAssignmentCourses.setValue(assignmentCoursesRes);
                }
            });
        }

        return filteredAssignmentCourses;
    }

    /**
     * Filter the assignment courses and set the value to {@link #filteredAssignmentCourses}
     *
     * @param assignmentCoursesRes unfiltered assignment courses
     */
    private void setFilteredAssignmentCourses(RemoteResource<List<MoodleAssignmentCourse>> assignmentCoursesRes) {
        List<MoodleAssignmentCourse> filteredAssignmentCourses = filterAssignmentCourses(assignmentCoursesRes.data);
        switch (assignmentCoursesRes.status) {
            case RemoteResource.ERROR:
                String msg = assignmentCoursesRes.message;
                this.filteredAssignmentCourses.setValue(RemoteResource.error(msg, filteredAssignmentCourses));
                break;
            case RemoteResource.LOADING:
                this.filteredAssignmentCourses.setValue(RemoteResource.loading(filteredAssignmentCourses));
                break;
            case RemoteResource.SUCCESS:
                this.filteredAssignmentCourses.setValue(RemoteResource.success(filteredAssignmentCourses));
                break;
        }
    }

    /**
     * Filter courses by removing the courses with no assignment
     *
     * @param assignmentCourses
     * @return filtered courses
     */
    @VisibleForTesting
    List<MoodleAssignmentCourse> filterAssignmentCourses(List<MoodleAssignmentCourse> assignmentCourses) {
        List<MoodleAssignmentCourse> filteredCourses = new ArrayList<>();

        for (MoodleAssignmentCourse course : assignmentCourses) {

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

        return filteredCourses;
    }

    /**
     * Returns an assignment submission
     *
     * @param assignId the assignment id
     * @return {@link LiveData} instance which contains the assignment submission
     */
    public LiveData<RemoteResource<MoodleAssignmentSubmission>> getAssignmentSubmission(int assignId) {
        if (assignmentSubmission == null || assignmentSubmission.getValue() == null
                || assignmentSubmission.getValue().data == null
                || assignmentSubmission.getValue().data.getFeedback() == null
                || assignmentSubmission.getValue().data.getFeedback().getGrade() == null
                || assignmentSubmission.getValue().data.getFeedback().getGrade().getAssignment() != assignId) {
            // Get an instance from the repository
            this.assignmentSubmission = repository.getAssignmentSubmission(assignId);
        }

        return assignmentSubmission;
    }

    /**
     * Set a boolean indicating whether past assignments should be displayed
     * <p>
     * The boolean value is set in the preferences
     *
     * @param display true if past assignments should be displayed
     */
    public void setDisplayPastAssignments(boolean display) {
        SharedPreferences settings = getApplication().getSharedPreferences(MOODLE_PREFS,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(DISPLAY_PAST_ASSIGNMENTS_PREF, display);
        editor.apply();

        if (assignmentCourses != null) {
            RemoteResource<List<MoodleAssignmentCourse>> remoteResource = assignmentCourses.getValue();
            if (remoteResource != null)
                setFilteredAssignmentCourses(remoteResource);
        }
    }

    /**
     * Returns true if the past assignments needs to be displayed
     * <p>
     * The boolean value is obtained from the preferences.
     *
     * @return true if the past assignments needs to be displayed
     */
    public boolean isDisplayPastAssignments() {
        SharedPreferences settings = getApplication().getSharedPreferences(MOODLE_PREFS,
                Context.MODE_PRIVATE);
        return settings.getBoolean(DISPLAY_PAST_ASSIGNMENTS_PREF, true);
    }

    /**
     * Save index of the type of sort selected by the user in the preferences
     *
     * @param index index of the type of sort selected by the user
     */
    public void setAssignmentsSortIndex(int index) {
        SharedPreferences settings = getApplication().getSharedPreferences(MOODLE_PREFS,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        int currentSortOrder = getAssignmentsSortOrder();
        if (getAssignmentsSortIndex() == index) {
            currentSortOrder *= -1;
            editor.putInt(SORT_ORDER_ASSIGNMENTS_PREF, currentSortOrder);
        } else {
            currentSortOrder = SORT_ASC;
            editor.putInt(SORT_ORDER_ASSIGNMENTS_PREF, currentSortOrder);
        }
        editor.putInt(SORT_ASSIGNMENTS_PREF, index);
        editor.apply();
    }

    /**
     * Returns the index of the type of sort selected by the user
     *
     * @return index of the type of sort selected by the user
     */
    public int getAssignmentsSortIndex() {
        SharedPreferences settings = getApplication().getSharedPreferences(MOODLE_PREFS,
                Context.MODE_PRIVATE);

        return settings.getInt(SORT_ASSIGNMENTS_PREF, SORT_BY_DATE);
    }

    private int getAssignmentsSortOrder() {
        SharedPreferences settings = getApplication().getSharedPreferences(MOODLE_PREFS,
                Context.MODE_PRIVATE);

        return settings.getInt(SORT_ORDER_ASSIGNMENTS_PREF, SORT_ASC);
    }

    /**
     * Returns the {@link Comparator} instance that can be used to sort the assignments
     *
     * @return {@link Comparator} instance that can be used to sort the assignments
     */
    public Comparator<MoodleAssignment> getAssignmentsSortComparator() {
        int sort = getAssignmentsSortIndex();
        int sortSorder = getAssignmentsSortOrder();

        return (a1, a2) -> {
            if (sort == SORT_BY_DATE) {
                if (sortSorder == SORT_ASC)
                    return a1.getDueDateObj().compareTo(a2.getDueDateObj());
                else
                    return a2.getDueDateObj().compareTo(a1.getDueDateObj());
            } else {
                if (sortSorder == SORT_ASC)
                    return a1.getName().compareTo(a2.getName());
                else
                    return a2.getName().compareTo(a1.getName());
            }
        };
    }

    /**
     * Select an assignment
     * <p>
     * The selected assignment is stored in a {@link LiveData} instance which will trigger the
     * observers of that {@link LiveData} instance.
     *
     * @param selectedAssignment the selected an assignment
     */
    public void selectAssignment(MoodleAssignment selectedAssignment) {
        this.selectedAssignment.setValue(selectedAssignment);
    }

    /**
     * @return {@link LiveData} instance which contains the selected assignment
     */
    public LiveData<MoodleAssignment> getSelectedAssignment() {
        return selectedAssignment;
    }
}
