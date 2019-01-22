package ca.etsmtl.applets.etsmobile.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by gnut3ll4 on 15/12/15.
 */
@DatabaseTable(tableName = "monets_notification")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MonETSNotification implements Comparable<MonETSNotification> {

    @DatabaseField(id = true)
    @JsonProperty("Id")
    int id;

    @DatabaseField
    @JsonProperty("DossierId")
    int dossierId;

    @DatabaseField
    @JsonProperty("NotificationTexte")
    String notificationTexte;

    @DatabaseField
    @JsonProperty("NotificationDateDebutAffichage")
    Date notificationDateDebutAffichage;

    @DatabaseField
    @JsonProperty("NotificationApplicationNom")
    String notificationApplicationNom;

    @DatabaseField
    @JsonProperty("Url")
    String url;

    public MonETSNotification() {
    }

    public MonETSNotification(int id, int dossierId, String notificationTexte, Date notificationDateDebutAffichage, String notificationApplicationNom, String url) {
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
    public int compareTo(MonETSNotification another) {
        if(notificationDateDebutAffichage.after(another.getNotificationDateDebutAffichage())) {
            return -1;
        }
        if(notificationDateDebutAffichage.before(another.getNotificationDateDebutAffichage())) {
            return 1;
        }
        return 0;
    }
}
