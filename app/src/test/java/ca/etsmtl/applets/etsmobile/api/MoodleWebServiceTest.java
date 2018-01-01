package ca.etsmtl.applets.etsmobile.api;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import ca.etsmtl.applets.etsmobile.http.MoodleWebService;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleAssignment;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleAssignmentCourse;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleAssignmentCourses;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleAssignmentFeedback;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleAssignmentGrade;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleAssignmentLastAttempt;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleAssignmentSubmission;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleCourse;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleProfile;
import ca.etsmtl.applets.etsmobile.util.LiveDataCallAdapterFactory;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by Sonphil on 09-09-17.
 */
@RunWith(JUnit4.class)
public class MoodleWebServiceTest {

    /** Timeout in seconds **/
    private static final int TIME_OUT = 2;

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private MockWebServer mockWebServer;
    private MoodleWebService service;

    @Before
    public void createService() throws IOException {
        mockWebServer = new MockWebServer();
        service = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(MoodleWebService.class);
    }

    @After
    public void stopService() throws IOException {
        mockWebServer.shutdown();
    }

    private Object getValue(@NonNull Call call) throws InterruptedException {
        final Object[] data = new Object[1];
        final CountDownLatch latch = new CountDownLatch(1);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                data[0] = response.body();
                latch.countDown();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                latch.countDown();
            }
        });

        latch.await(TIME_OUT, TimeUnit.SECONDS);

        return data[0];
    }

    private <T> T getValue(@NonNull LiveData<T> liveData) throws InterruptedException {
        final Object[] data = new Object[1];
        CountDownLatch latch = new CountDownLatch(1);
        Observer<T> observer = new Observer<T>() {
            @Override
            public void onChanged(@Nullable T o) {
                data[0] = o;
                latch.countDown();
                liveData.removeObserver(this);
            }
        };
        liveData.observeForever(observer);
        latch.await(TIME_OUT, TimeUnit.SECONDS);
        //noinspection unchecked
        return (T) data[0];
    }

    private void enqueueResponse(String fileName) throws IOException {
        enqueueResponse(fileName, new HashMap<>());
    }

    private void enqueueResponse(String fileName, Map<String, String> headers) throws IOException {
        String file = "api-response/" + fileName;
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream(file);
        BufferedSource source = Okio.buffer(Okio.source(inputStream));
        MockResponse mockResponse = new MockResponse();
        for (Map.Entry<String, String> header : headers.entrySet()) {
            mockResponse.addHeader(header.getKey(), header.getValue());
        }
        mockWebServer.enqueue(mockResponse
                .setBody(source.readString(Charset.forName("UTF-8"))));
    }

    @Test
    public void getMoodleProfile() throws IOException, InterruptedException {
        enqueueResponse("profile.json");
        MoodleProfile profile = (MoodleProfile) getValue(service.getProfile("12345")).body;
        assertThat(profile, notNullValue());
        assertEquals("am12345", profile.getUsername());
        assertEquals("Bruce", profile.getFirstname());
        assertEquals("Wayne", profile.getLastname());
        assertEquals("Bruce Wayne", profile.getFullname());
        assertEquals("fr_ca", profile.getLang());
        assertEquals(12345, profile.getUserId());
        assertEquals("https://ena.etsmtl.ca/theme/image.php/boost_ets/core/1234/u/f1", profile.getUserPictureUrl());
    }

    @Test
    public void getMoodleCourses() throws IOException, InterruptedException {
        enqueueResponse("courses.json");
        List<MoodleCourse> courses = getValue(service.getCourses("1234", 1234)).body;
        assertThat(courses, notNullValue());
        assertEquals(20, courses.size());
        MoodleCourse course = courses.get(0);
        assertEquals(1705, course.getId());
        assertEquals("PRE010-Références", course.getShortname());
        assertEquals("PRE010 - volet développement professionnel - RÉFÉRENCES", course.getFullname());
        assertEquals("PRE010-References", course.getIdNumber());
    }

    @Test
    public void getMoodleAssignmentsCourses() throws IOException, InterruptedException {
        enqueueResponse("assignments.json");
        MoodleAssignmentCourses courses = (MoodleAssignmentCourses) getValue(service.getAssignmentCourses("1234", new int[1])).body;
        assertThat(courses, notNullValue());
        assertEquals(3, courses.getCourses().size());
        MoodleAssignmentCourse course = courses.getCourses().get(2);
        assertEquals(5599, course.getId());
        assertThat(course.getFullName(), is("GTI350-01-02 Conception et évaluation des interfaces utilisateurs (H2017)"));
        assertThat(course.getShortName(), is("S20171-GTI350-01-02"));
        List<MoodleAssignment> assignments = course.getAssignments();
        assertThat(assignments, notNullValue());
        assertEquals(6, assignments.size());
        MoodleAssignment assignment = assignments.get(0);
        assertThat(assignment, notNullValue());
        assertEquals(11356, assignment.getId());
        assertEquals(262197, assignment.getCmid());
        assertThat(assignment.getName(), is("Remise Laboratoire 1 (scoreboard snowboard)"));
        assertThat(assignment.getGrade(), is(100));
        Date dueDate = assignment.getDueDateObj();
        Date expectedDueDate = new Date(1486184100000L);
        assertTrue(dueDate.toString() + " Expected: " + expectedDueDate.toString(), dueDate.equals(expectedDueDate));
        assertThat(assignment.getIntro(), is("<p>Veuillez remettre votre projet au complet contenant tout votre code source dans un fichier .zip</p>\r\n<p>Si vous avez des instructions supplémentaires facilitant l'exécution de code, veuillez inclure dans votre .zip un fichier lisezmoi.txt</p>\r\n<p>Vous pouvez aussi écrire un message lors de la remise si vous avez des commentaires pertinents par rapport à votre remise.</p>\r\n<p>Pondération: 6% (dont 1% pour le walkthrough, à faire individuellement)</p>\r\n<p></p>\r\n<p>Remise :  30 janvier 2017, au <strong>début</strong> de la séance de laboratoire (Groupe 1)</p>\r\n<p>                3 février 2017, au <strong>début</strong> de la séance de laboratoire (Groupe 2)</p>\r\n<p></p>\r\n<p></p>"));
    }

    @Test
    public void getMoodleAssignmentSubmission() throws IOException, InterruptedException {
        enqueueResponse("assignment_submission.json");
        MoodleAssignmentSubmission moodleSubmission = (MoodleAssignmentSubmission) getValue(service.getAssignmentSubmission("1234", 1234)).body;
        assertThat(moodleSubmission, notNullValue());

        MoodleAssignmentLastAttempt lastAttempt = moodleSubmission.getLastAttempt();
        assertThat(lastAttempt, notNullValue());
        MoodleAssignmentLastAttempt.Submission submission = lastAttempt.getSubmission();
        assertThat(submission, notNullValue());
        MoodleAssignmentLastAttempt.TeamSubmission teamSubmission = lastAttempt.getTeamSubmission();
        assertThat(teamSubmission, notNullValue());
        assertThat(lastAttempt.getGradingStatus(), is("graded"));
        assertTrue(lastAttempt.isGraded());
        assertFalse(lastAttempt.isSubmitted());
        assertTrue(lastAttempt.isTeamSubmitted());

        MoodleAssignmentFeedback feedback = moodleSubmission.getFeedback();
        assertThat(feedback, notNullValue());

        MoodleAssignmentGrade grade = feedback.getGrade();
        assertThat(grade, notNullValue());
    }
}
