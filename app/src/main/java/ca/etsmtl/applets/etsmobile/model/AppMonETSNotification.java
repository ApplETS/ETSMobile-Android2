package ca.etsmtl.applets.etsmobile.model;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import ca.etsmtl.applets.etsmobilenotifications.model.MonETSNotification;

/**
 * Created by gnut3ll4 on 15/12/15.
 */
@DatabaseTable(tableName = "monets_notification")
public class AppMonETSNotification implements Comparable<AppMonETSNotification> {

    @DatabaseField(id = true)
    @SerializedName("Id")
    int id;

    @DatabaseField
    @SerializedName("DossierId")
    int dossierId;

    @DatabaseField
    @SerializedName("NotificationTexte")
    String notificationTexte;

    @DatabaseField
    @SerializedName("NotificationDateDebutAffichage")
    Date notificationDateDebutAffichage;

    @DatabaseField
    @SerializedName("NotificationApplicationNom")
    String notificationApplicationNom;

    @DatabaseField
    @SerializedName("Url")
    String url;

    public AppMonETSNotification() {

    }

    public AppMonETSNotification(MonETSNotification monETSNotification) {
        id = monETSNotification.getId();
        dossierId = monETSNotification.getDossierId();
        notificationTexte = monETSNotification.getNotificationTexte();
        notificationDateDebutAffichage = monETSNotification.getNotificationDateDebutAffichage();
        notificationApplicationNom = monETSNotification.getNotificationApplicationNom();
        url = monETSNotification.getUrl();
    }

    public AppMonETSNotification(int id, int dossierId, String notificationTexte, Date notificationDateDebutAffichage, String notificationApplicationNom, String url) {
        this.id = id;
        this.dossierId = dossierId;
        this.notificationTexte = notificationTexte;
        this.notificationDateDebutAffichage = notificationDateDebutAffichage;
        this.notificationApplicationNom = notificationApplicationNom;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public int getDossierId() {
        return dossierId;
    }

    public String getNotificationTexte() {
        return notificationTexte;
    }

    public Date getNotificationDateDebutAffichage() {
        return notificationDateDebutAffichage;
    }

    public String getNotificationApplicationNom() {
        return notificationApplicationNom;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int compareTo(AppMonETSNotification another) {
        if(notificationDateDebutAffichage.after(another.getNotificationDateDebutAffichage())) {
            return -1;
        }
        if(notificationDateDebutAffichage.before(another.getNotificationDateDebutAffichage())) {
            return 1;
        }
        return 0;
    }
}
