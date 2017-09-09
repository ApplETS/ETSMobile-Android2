package ca.etsmtl.applets.etsmobile.http;

import android.support.annotation.NonNull;

import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleAssignmentCourses;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleAssignmentSubmission;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleCourses;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleProfile;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleToken;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Sonphil on 08-09-17.
 */

public interface MoodleWebService {
    @GET("login/token.php?service=moodle_mobile_app")
    Call<MoodleToken> getToken(@Query("username") @NonNull String userName, @Query("password") @NonNull String password);

    @GET("webservice/rest/server.php?moodlewsrestformat=json&wsfunction=core_webservice_get_site_info")
    Call<MoodleProfile> getProfile(@Query("wstoken") @NonNull String token);

    @GET("webservice/rest/server.php?moodlewsrestformat=json&wsfunction=core_enrol_get_users_courses")
    Call<MoodleCourses> getCourses(@Query("wstoken") @NonNull String token, @Query("userid") int userId);

    @GET("webservice/rest/server.php?moodlewsrestformat=json&wsfunction=mod_assign_get_assignments")
    Call<MoodleAssignmentCourses> getAssignmentCourses(@Query("wstoken") @NonNull String token, @Query("courseids[]") int[] coursesIds);

    @GET("webservice/rest/server.php?moodlewsrestformat=json&wsfunction=mod_assign_get_submission_status")
    Call<MoodleAssignmentSubmission> getAssignmentSubmission(@Query("wstoken") @NonNull String token, @Query("assignid") int assignId);
}
