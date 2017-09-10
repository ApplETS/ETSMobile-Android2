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
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleAssignment;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleAssignmentCourse;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleAssignmentSubmission;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleCourses;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleProfile;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleRepository;
import ca.etsmtl.applets.etsmobile.model.RemoteResource;

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
    public void getProfile() {
        MutableLiveData<RemoteResource<MoodleProfile>> liveData = new MutableLiveData<>();
        when(repository.getProfile()).thenReturn(liveData);
        Observer<RemoteResource<MoodleProfile>> observer = mock(Observer.class);
        viewModel.getProfile().observeForever(observer);
        verify(observer, never()).onChanged(any(RemoteResource.class));
        RemoteResource<MoodleProfile> remoteRes = RemoteResource.loading(null);
        liveData.setValue(remoteRes);
        verify(observer).onChanged(remoteRes);
        reset(observer);
        remoteRes = RemoteResource.success(new MoodleProfile());
        liveData.setValue(remoteRes);
        verify(observer).onChanged(remoteRes);
    }

    @Test
    public void getAssignmentCourses() {
        MutableLiveData<RemoteResource<List<MoodleAssignmentCourse>>> liveData = new MutableLiveData<>();
        when(repository.getAssignmentCourses()).thenReturn(liveData);
        Observer<RemoteResource<List<MoodleAssignmentCourse>>> observer = mock(Observer.class);
        viewModel.getAssignmentCourses().observeForever(observer);
        verify(observer, never()).onChanged(any(RemoteResource.class));
        RemoteResource<List<MoodleAssignmentCourse>> remoteRes = RemoteResource.loading(null);
        liveData.setValue(remoteRes);
        verify(observer).onChanged(remoteRes);
        reset(observer);
        List<MoodleAssignmentCourse> list = new ArrayList<>();
        remoteRes = RemoteResource.success(list);
        liveData.setValue(remoteRes);
        verify(observer).onChanged(remoteRes);
    }

    @Test
    public void filterAssignmentCoursesNoAssignments() {
        createMockSharedPreferences();

        List<MoodleAssignmentCourse> courses = new ArrayList<>();
        MutableLiveData<RemoteResource<List<MoodleAssignmentCourse>>> liveData = new MutableLiveData<>();
        when(repository.getAssignmentCourses()).thenReturn(liveData);
        viewModel.getAssignmentCourses();
        liveData.setValue(RemoteResource.success(courses));
        assertEquals(0, viewModel.getAssignmentCourses().getValue().data.size());
        assertEquals(0, viewModel.filterAssignmentCourses().getValue().size());

        List<MoodleAssignment> assignments = new ArrayList<>();
        MoodleAssignment assignment = new MoodleAssignment();
        long unixTimeFuture = (TimeUnit.MILLISECONDS.toSeconds(new Date().getTime()) + TimeUnit.DAYS.toSeconds(1));
        assignment.setDueDate(unixTimeFuture);
        assignments.add(assignment);
        courses.add(new MoodleAssignmentCourse(0, "", "", 1510358400, assignments));
        assertEquals(1, viewModel.getAssignmentCourses().getValue().data.size());
        assertEquals(1, viewModel.filterAssignmentCourses().getValue().size());
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
        MutableLiveData<RemoteResource<List<MoodleAssignmentCourse>>> liveData = new MutableLiveData<>();
        when(repository.getAssignmentCourses()).thenReturn(liveData);
        viewModel.getAssignmentCourses();
        liveData.setValue(RemoteResource.success(courses));
        assertEquals(0, viewModel.getAssignmentCourses().getValue().data.size());
        assertEquals(0, viewModel.filterAssignmentCourses().getValue().size());


        // Test with only a past assignment
        assignments.add(pastAssignment);
        courses.add(new MoodleAssignmentCourse(0, "", "", 1510358400, assignments));
        assertEquals(1, viewModel.getAssignmentCourses().getValue().data.size());
        assertEquals(0, viewModel.filterAssignmentCourses().getValue().size());

        // Test with a past assignment and a future assignment
        assignments.add(futureAssignment);
        assertEquals(1, viewModel.getAssignmentCourses().getValue().data.size());
        assertEquals(1, viewModel.filterAssignmentCourses().getValue().size());

        // Test with only a future assignment
        assignments.clear();
        assignments.add(futureAssignment);
        courses.add(new MoodleAssignmentCourse(0, "", "", 1510358400, assignments));
        assertEquals(2, viewModel.getAssignmentCourses().getValue().data.size());
        assertEquals(2, viewModel.filterAssignmentCourses().getValue().size());
    }

    @Test
    public void getCourses() {
        MutableLiveData<RemoteResource<MoodleCourses>> liveData = new MutableLiveData<>();
        when(repository.getCourses()).thenReturn(liveData);
        Observer<RemoteResource<MoodleCourses>> observer = mock(Observer.class);
        viewModel.getCourses().observeForever(observer);
        verify(observer, never()).onChanged(any(RemoteResource.class));
        RemoteResource<MoodleCourses> remoteRes = RemoteResource.loading(null);
        liveData.setValue(remoteRes);
        verify(observer).onChanged(remoteRes);
        reset(observer);
        remoteRes = RemoteResource.success(new MoodleCourses());
        liveData.setValue(remoteRes);
        verify(observer).onChanged(remoteRes);
    }

    @Test
    public void getAssignmentSubmission() {
        MutableLiveData<RemoteResource<MoodleAssignmentSubmission>> liveData = new MutableLiveData<>();
        when(repository.getAssignmentSubmission(anyInt())).thenReturn(liveData);
        Observer<RemoteResource<MoodleAssignmentSubmission>> observer = mock(Observer.class);
        viewModel.getAssignmentSubmission(anyInt()).observeForever(observer);
        verify(observer, never()).onChanged(any(RemoteResource.class));
        RemoteResource<MoodleAssignmentSubmission> remoteRes = RemoteResource.loading(null);
        liveData.setValue(remoteRes);
        verify(observer).onChanged(remoteRes);
        reset(observer);
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
        createMockSharedPreferences();
        assertFalse(viewModel.isDisplayPastAssignments());
        when(prefs.getBoolean(eq(DISPLAY_PAST_ASSIGNMENTS_PREF), anyBoolean()))
                .thenReturn(true);
        assertTrue(viewModel.isDisplayPastAssignments());
    }

    @Test
    public void setDisplayPastAssignments() {
        createMockSharedPreferences();

        viewModel.setDisplayPastAssignments(true);

        when(prefs.getBoolean(eq(DISPLAY_PAST_ASSIGNMENTS_PREF), anyBoolean()))
                .thenReturn(true);

        assertTrue(viewModel.isDisplayPastAssignments());

        viewModel.setDisplayPastAssignments(false);

        when(prefs.getBoolean(eq(DISPLAY_PAST_ASSIGNMENTS_PREF), anyBoolean()))
                .thenReturn(false);

        assertFalse(viewModel.isDisplayPastAssignments());

    }

    @Test
    public void getSortIndex() {
        createMockSharedPreferences();
        assertEquals(viewModel.getAssignmentsSortIndex(), MoodleViewModel.SORT_ALPHA);
    }

    @Test
    public void setSortIndex() {
        createMockSharedPreferences();

        viewModel.setAssignmentsSort(MoodleViewModel.SORT_BY_DATE);

        when(prefs.getInt(eq(SORT_ASSIGNMENTS_PREF), anyInt()))
                .thenReturn(MoodleViewModel.SORT_BY_DATE);

        assertEquals(viewModel.getAssignmentsSortIndex(), MoodleViewModel.SORT_BY_DATE);
    }
}
