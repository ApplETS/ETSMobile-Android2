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
 * Interface définisant des « endpoints » de l'API de Moodle
 * <p>
 * Chaque fonction retourne une instance de {@link Call} qui peut être utilisée afin de permettre à
 * Retrofit d'envoyer une requête au serveur web.
 * <p>
 * Created by Sonphil on 08-09-17.
 */

public interface MoodleWebService {
    /**
     * Chaque requête doit être identifiée par un jeton unique. Cette méthode retourne un
     * {@link Call} permettant d'obtenir ce jeton.
     *
     * @param userName le nom d'utilisateur qui est le code d'accès universel
     * @param password le mot de passe de l'utilisateur
     * @return {@link Call} permettant d'obtenir le jeton unique
     */
    @GET("login/token.php?service=moodle_mobile_app")
    Call<MoodleToken> getToken(@Query("username") @NonNull String userName, @Query("password") @NonNull String password);

    /**
     * Retour d'un {@link Call} permettant d'obtenir le profil de l'utilisateur
     * L'API va retourner des informations à propos de l'utilisateur incluant son nom d'utilisateur,
     * son nom, son prénom et son id. De plus, l'API retourne aussi des informations à propos du
     * site, des webservices ainsi que les actions autorisées.
     *
     * @param token jeton
     * @return {@link Call} permettant d'obtenir le profil de l'utilisateur
     */
    @GET("webservice/rest/server.php?moodlewsrestformat=json&wsfunction=core_webservice_get_site_info")
    Call<MoodleProfile> getProfile(@Query("wstoken") @NonNull String token);

    /**
     * Retour d'un {@link Call} permettant d'obtenir les cours de l'utilisateur
     *
     * @param token  jeton
     * @param userId identifiant de l'utilisateur
     * @return @link Call} permettant d'obtenir les cours de l'utilisateur
     */
    @GET("webservice/rest/server.php?moodlewsrestformat=json&wsfunction=core_enrol_get_users_courses")
    Call<MoodleCourses> getCourses(@Query("wstoken") @NonNull String token, @Query("userid") int userId);

    /**
     * Retour d'un {@link Call} permettant d'obtenir un tableau de cours. Chaque cours contient les
     * devoirs visibles à l'utilisateur.
     *
     * @param token      jeton
     * @param coursesIds Les ids des cours devant être retournés.
     * @return call
     */
    @GET("webservice/rest/server.php?moodlewsrestformat=json&wsfunction=mod_assign_get_assignments")
    Call<MoodleAssignmentCourses> getAssignmentCourses(@Query("wstoken") @NonNull String token, @Query("courseids[]") int[] coursesIds);

    /**
     * Retour d'un {@link Call} permettant d'obtenir le statut de la remise d'un devoir pour un
     * utilisateur donné
     *
     * @param token    jeton
     * @param assignId id du devoir
     * @return {@link Call} permettant d'obtenir le statut de la remise d'un devoir
     */
    @GET("webservice/rest/server.php?moodlewsrestformat=json&wsfunction=mod_assign_get_submission_status")
    Call<MoodleAssignmentSubmission> getAssignmentSubmission(@Query("wstoken") @NonNull String token, @Query("assignid") int assignId);
}
