package ca.etsmtl.applets.etsmobile.view_model;

import android.app.Application;
import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ca.etsmtl.applets.etsmobile.model.moodle.MoodleAssignment;
import ca.etsmtl.applets.etsmobile.model.moodle.MoodleAssignmentCourse;
import ca.etsmtl.applets.etsmobile.model.moodle.MoodleAssignmentSubmission;
import ca.etsmtl.applets.etsmobile.model.moodle.MoodleCourse;
import ca.etsmtl.applets.etsmobile.model.moodle.MoodleCourses;
import ca.etsmtl.applets.etsmobile.model.RemoteResource;
import ca.etsmtl.applets.etsmobile.repository.MoodleRepository;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Sonphil on 09-09-17.
 */
@RunWith(JUnit4.class)
public class MoodleViewModelTest {

    private static final String DISPLAY_PAST_ASSIGNMENTS_PREF = "DisplayPastAssignmentPref";
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

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private MoodleViewModel viewModel;
    private MoodleRepository repository = mock(MoodleRepository.class);
    private Application application = mock(Application.class);
    private SharedPreferences prefs = mock(SharedPreferences.class);
    private SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);

    @Before
    public void setUp() {
        viewModel = new MoodleViewModel(application, repository);
    }

    @Test
    public void getCourses() {
        // Prepare LiveData containing the courses
        MutableLiveData<RemoteResource<List<MoodleCourse>>> liveData = new MutableLiveData<>();
        when(repository.getCourses()).thenReturn(liveData);

        // Prepare observer
        Observer<RemoteResource<List<MoodleCourse>>> observer = mock(Observer.class);
        viewModel.getCourses().observeForever(observer);
        verify(observer, never()).onChanged(any(RemoteResource.class));

        // Set a value and check if onChanged has been called
        RemoteResource<List<MoodleCourse>> remoteRes = RemoteResource.loading(null);
        liveData.setValue(remoteRes);
        verify(observer).onChanged(remoteRes);

        reset(observer);

        // Set another value and check if onChanged has been called again
        remoteRes = RemoteResource.success(new MoodleCourses());
        liveData.setValue(remoteRes);
        verify(observer).onChanged(remoteRes);
    }

    @Test
    public void getAssignmentCourses() {
        // Prepare LiveData
        MutableLiveData<RemoteResource<List<MoodleAssignmentCourse>>> liveData = new MutableLiveData<>();
        when(repository.getAssignmentCourses()).thenReturn(liveData);

        // Prepare Observer
        Observer<RemoteResource<List<MoodleAssignmentCourse>>> observer = mock(Observer.class);
        viewModel.getAssignmentCourses().observeForever(observer);
        verify(observer, never()).onChanged(any(RemoteResource.class));

        // Set a value and check if onChanged was called
        RemoteResource<List<MoodleAssignmentCourse>> remoteRes = RemoteResource.loading(null);
        liveData.setValue(remoteRes);
        verify(observer).onChanged(remoteRes);

        reset(observer);

        // Set another value and check if onChanged was called
        List<MoodleAssignmentCourse> list = new ArrayList<>();
        remoteRes = RemoteResource.success(list);
        liveData.setValue(remoteRes);
        verify(observer).onChanged(any(RemoteResource.class));
    }

    @Test
    public void filterAssignmentCoursesNoAssignments() {
        createMockSharedPreferences();

        // Set an empty courses list
        List<MoodleAssignmentCourse> courses = new ArrayList<>();
        assertEquals(0, viewModel.filterAssignmentCourses(courses).size());

        // Set up assignments list and an assignment without adding it to the list
        List<MoodleAssignment> assignments = new ArrayList<>();
        MoodleAssignment assignment = new MoodleAssignment();
        // Set a future date because past assignments are not displayed
        long unixTimeFuture = (TimeUnit.MILLISECONDS.toSeconds(new Date().getTime()) + TimeUnit.DAYS.toSeconds(1));
        assignment.setDueDate(unixTimeFuture);

        // Add course to courses list
        courses.add(new MoodleAssignmentCourse(0, "", "", 1510358400, assignments));
        assertEquals(0, viewModel.filterAssignmentCourses(courses).size());

        // Add assignment to assignments list
        assignments.add(assignment);
        assertEquals(1, viewModel.filterAssignmentCourses(courses).size());
    }

    @Test
    public void filterAssignmentsCoursesPastAssignments() {
        createMockSharedPreferences();

        // Prepare dates
        long unixTimeNowMs = (TimeUnit.MILLISECONDS.toSeconds(new Date().getTime()));
        long unixTimeFutureSec = unixTimeNowMs + TimeUnit.DAYS.toSeconds(1);
        long unixTimePastSec = unixTimeNowMs - TimeUnit.DAYS.toSeconds(1);

        // Prepare assignments
        List<MoodleAssignment> assignments = new ArrayList<>();
        MoodleAssignment futureAssignment = new MoodleAssignment();
        futureAssignment.setDueDate(unixTimeFutureSec);
        MoodleAssignment pastAssignment = new MoodleAssignment();
        pastAssignment.setDueDate(unixTimePastSec);

        // Prepare courses list
        List<MoodleAssignmentCourse> courses = new ArrayList<>();
        assertEquals(0, viewModel.filterAssignmentCourses(courses).size());

        // Test with only a past assignment
        assignments.add(pastAssignment);
        courses.add(new MoodleAssignmentCourse(0, "", "", 1510358400, assignments));
        assertEquals(0, viewModel.filterAssignmentCourses(courses).size());

        // Test with a past assignment and a future assignment
        assignments.add(futureAssignment);
        assertEquals(1, viewModel.filterAssignmentCourses(courses).size());

        // Test with only a future assignment
        assignments.clear();
        assignments.add(futureAssignment);
        courses.add(new MoodleAssignmentCourse(0, "", "", 1510358400, assignments));
        assertEquals(2, viewModel.filterAssignmentCourses(courses).size());
    }

    @Test
    public void getAssignmentSubmission() {
        // Prepare LiveData
        MutableLiveData<RemoteResource<MoodleAssignmentSubmission>> liveData = new MutableLiveData<>();
        when(repository.getAssignmentSubmission(anyInt())).thenReturn(liveData);

        // Prepare Observer
        Observer<RemoteResource<MoodleAssignmentSubmission>> observer = mock(Observer.class);
        viewModel.getAssignmentSubmission(anyInt()).observeForever(observer);
        verify(observer, never()).onChanged(any(RemoteResource.class));

        // Set a value and check if onChanged has been called
        RemoteResource<MoodleAssignmentSubmission> remoteRes = RemoteResource.loading(null);
        liveData.setValue(remoteRes);
        verify(observer).onChanged(remoteRes);

        reset(observer);

        // Set another value and check if onChanged has been called again
        remoteRes = RemoteResource.success(new MoodleAssignmentSubmission());
        liveData.setValue(remoteRes);
        verify(observer).onChanged(remoteRes);
    }

    private void createMockSharedPreferences() {
        // Return the mocked SharedPreferences when requesting it
        when(application.getSharedPreferences(anyString(), anyInt())).thenReturn(prefs);

        /*
        Mocking reading the SharedPreferences as is the mocked SharedPreferences was written
        correctly
         */
        when(prefs.getBoolean(eq(DISPLAY_PAST_ASSIGNMENTS_PREF), anyBoolean()))
                .thenReturn(false);
        when(prefs.getInt(eq(SORT_ASSIGNMENTS_PREF), anyInt()))
                .thenReturn(MoodleViewModel.SORT_ALPHA);

        // Mocking a successful commit
        when(editor.commit()).thenReturn(true);

        // Return the mocked editor when requesting it
        when(prefs.edit()).thenReturn(editor);
    }

    @Test
    public void getDisplayPastAssignments() {
        // The display past assignments pref is set to false
        createMockSharedPreferences();

        assertFalse(viewModel.isDisplayPastAssignments());

        when(prefs.getBoolean(eq(DISPLAY_PAST_ASSIGNMENTS_PREF), anyBoolean())).thenReturn(true);
        assertTrue(viewModel.isDisplayPastAssignments());
    }

    @Test
    public void setDisplayPastAssignments() {
        createMockSharedPreferences();

        // Test set true
        viewModel.setDisplayPastAssignments(true);
        verify(editor).putBoolean(DISPLAY_PAST_ASSIGNMENTS_PREF, true);
        when(prefs.getBoolean(eq(DISPLAY_PAST_ASSIGNMENTS_PREF), anyBoolean())).thenReturn(true);
        assertTrue(viewModel.isDisplayPastAssignments());

        // Test set false
        reset(editor);
        viewModel.setDisplayPastAssignments(false);
        verify(editor).putBoolean(DISPLAY_PAST_ASSIGNMENTS_PREF, false);
        when(prefs.getBoolean(eq(DISPLAY_PAST_ASSIGNMENTS_PREF), anyBoolean())).thenReturn(false);
        assertFalse(viewModel.isDisplayPastAssignments());

    }

    @Test
    public void getAssignmentsSortIndex() {
        createMockSharedPreferences();
        assertEquals(viewModel.getAssignmentsSortIndex(), MoodleViewModel.SORT_ALPHA);
    }

    @Test
    public void sortByDate() {
        createMockSharedPreferences();

        // Prepare assignments
        MoodleAssignment assign1 = new MoodleAssignment();
        assign1.setDueDate(TimeUnit.SECONDS.toMillis(1505079000));

        MoodleAssignment assign2 = new MoodleAssignment();
        assign2.setDueDate(TimeUnit.SECONDS.toMillis(1507593600));

        // Verify that editor putInt method has been called correctly by the view model
        viewModel.setAssignmentsSortIndex(MoodleViewModel.SORT_BY_DATE);
        verify(editor).putInt(SORT_ASSIGNMENTS_PREF, MoodleViewModel.SORT_BY_DATE);

        // Test the comparator for sorting by date
        when(prefs.getInt(eq(SORT_ASSIGNMENTS_PREF), anyInt()))
                .thenReturn(MoodleViewModel.SORT_BY_DATE);
        when(prefs.getInt(eq(SORT_ORDER_ASSIGNMENTS_PREF), anyInt()))
                .thenReturn(SORT_ASC);
        assertEquals(viewModel.getAssignmentsSortIndex(), MoodleViewModel.SORT_BY_DATE);
        Comparator<MoodleAssignment> comparator = viewModel.getAssignmentsSortComparator();
        assertTrue(comparator.compare(assign1, assign2) < 0);
        when(prefs.getInt(eq(SORT_ORDER_ASSIGNMENTS_PREF), anyInt()))
                .thenReturn(SORT_DESC);
        comparator = viewModel.getAssignmentsSortComparator();
        assertTrue(comparator.compare(assign1, assign2) > 0);
    }

    @Test
    public void sortAlphabetically() {
        createMockSharedPreferences();

        // Prepare assignments
        MoodleAssignment assign1 = new MoodleAssignment();
        assign1.setName("cca");

        MoodleAssignment assign2 = new MoodleAssignment();
        assign2.setName("aab");

        // Verify that editor putInt method has been called correctly by the view model
        viewModel.setAssignmentsSortIndex(MoodleViewModel.SORT_BY_DATE);
        verify(editor).putInt(SORT_ASSIGNMENTS_PREF, MoodleViewModel.SORT_BY_DATE);


        // Test the comparator for sorting alphabetically
        when(prefs.getInt(eq(SORT_ASSIGNMENTS_PREF), anyInt()))
                .thenReturn(MoodleViewModel.SORT_ALPHA);
        when(prefs.getInt(eq(SORT_ORDER_ASSIGNMENTS_PREF), anyInt()))
                .thenReturn(SORT_ASC);
        Comparator<MoodleAssignment> comparator = viewModel.getAssignmentsSortComparator();
        assertEquals(viewModel.getAssignmentsSortIndex(), MoodleViewModel.SORT_ALPHA);
        comparator = viewModel.getAssignmentsSortComparator();
        assertTrue(comparator.compare(assign1, assign2) > 0);
        when(prefs.getInt(eq(SORT_ORDER_ASSIGNMENTS_PREF), anyInt()))
                .thenReturn(SORT_DESC);
        comparator = viewModel.getAssignmentsSortComparator();
        assertTrue(comparator.compare(assign1, assign2) < 0);
    }
}
