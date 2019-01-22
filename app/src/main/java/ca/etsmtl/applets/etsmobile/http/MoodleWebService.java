package ca.etsmtl.applets.etsmobile.http;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import ca.etsmtl.applets.etsmobile.model.ApiResponse;
import ca.etsmtl.applets.etsmobile.model.moodle.MoodleAssignmentCourses;
import ca.etsmtl.applets.etsmobile.model.moodle.MoodleAssignmentSubmission;
import ca.etsmtl.applets.etsmobile.model.moodle.MoodleCourse;
import ca.etsmtl.applets.etsmobile.model.moodle.MoodleProfile;
import ca.etsmtl.applets.etsmobile.model.moodle.MoodleToken;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface définisant des « endpoints » de l'API de Moodle
 * <p>
 * Chaque fonction retourne une instance de {@link LiveData} qui s'active lorsqu'il est observée.
 * Lors de l'activation, une reqquête est envoyée au serveur web.
 * <p>
 * Created by Sonphil on 08-09-17.
 */

public interface MoodleWebService {
    /**
     * Chaque requête doit être identifiée par un jeton unique. Cette méthode retourne un
     * {@link LiveData} permettant d'obtenir ce jeton.
     *
     * @param userName le nom d'utilisateur qui est le code d'accès universel
     * @param password le mot de passe de l'utilisateur
     * @return {@link LiveData} permettant d'obtenir le jeton unique
     */
    @GET("login/token.php?service=moodle_mobile_app")
    LiveData<ApiResponse<MoodleToken>> getToken(@Query("username") @NonNull String userName, @Query("password") @NonNull String password);

    /**
     * Retour d'un {@link LiveData} permettant d'obtenir le profil de l'utilisateur
     * L'API va retourner des informations à propos de l'utilisateur incluant son nom d'utilisateur,
     * son nom, son prénom et son id. De plus, l'API retourne aussi des informations à propos du
     * site, des webservices ainsi que les actions autorisées.
     *
     * @param token jeton
     * @return {@link LiveData} permettant d'obtenir le profil de l'utilisateur
     */
    @GET("webservice/rest/server.php?moodlewsrestformat=json&wsfunction=core_webservice_get_site_info")
    LiveData<ApiResponse<MoodleProfile>> getProfile(@Query("wstoken") @NonNull String token);

    /**
     * Retour d'un {@link LiveData} permettant d'obtenir les cours de l'utilisateur
     *
     * @param token  jeton
     * @param userId identifiant de l'utilisateur
     * @return {@link LiveData} permettant d'obtenir les cours de l'utilisateur
     */
    @GET("webservice/rest/server.php?moodlewsrestformat=json&wsfunction=core_enrol_get_users_courses")
    LiveData<ApiResponse<List<MoodleCourse>>> getCourses(@Query("wstoken") @NonNull String token, @Query("userid") int userId);

    /**
     * Retour d'un {@link LiveData} permettant d'obtenir un tableau de cours. Chaque cours contient les
     * devoirs visibles à l'utilisateur.
     *
     * @param token      jeton
     * @param coursesIds Les ids des cours devant être retournés.
     * @return {@link LiveData}
     */
    @GET("webservice/rest/server.php?moodlewsrestformat=json&wsfunction=mod_assign_get_assignments")
    LiveData<ApiResponse<MoodleAssignmentCourses>> getAssignmentCourses(@Query("wstoken") @NonNull String token, @Query("courseids[]") int[] coursesIds);

    /**
     * Retour d'un {@link LiveData} permettant d'obtenir le statut de la remise d'un devoir pour un
     * utilisateur donné
     *
     * @param token    jeton
     * @param assignId id du devoir
     * @return {@link LiveData} permettant d'obtenir le statut de la remise d'un devoir
     */
    @GET("webservice/rest/server.php?moodlewsrestformat=json&wsfunction=mod_assign_get_submission_status")
    LiveData<ApiResponse<MoodleAssignmentSubmission>> getAssignmentSubmission(@Query("wstoken") @NonNull String token, @Query("assignid") int assignId);
}
