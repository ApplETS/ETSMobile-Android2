package ca.etsmtl.applets.etsmobile.http;

import java.util.List;

import ca.etsmtl.applets.etsmobile.model.MonETSNotification;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Interface définissant des endpoints pour MonÉTS utilisant {@link retrofit2.Retrofit}
 *
 * @author zaclimon
 */
public interface MonETSWebService {
    /**
     * Retourne toutes les notifications provenant de MonÉTS pour un utilisateur
     *
     * @param authToken Le jeton d'authentification d'un utilisateur
     * @return la liste de notifications pour un utilisateur
     */
    @GET("/api/notification")
    Call<List<MonETSNotification>> getAllNotifications(@Header("Cookie") String authToken);

    /**
     * Retourne uniquement les notifications non lues provenant de MonÉTS pour un utilisateur
     *
     * @param authToken Le jeton d'authentification d'un utilisateur
     * @return la liste de notifications pour un utilisateur
     */
    @GET("/api/notification/1")
    Call<List<MonETSNotification>> getUnreadNotifications(@Header("Cookie") String authToken);
}
