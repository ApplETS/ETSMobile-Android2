package ca.etsmtl.applets.etsmobilenotifications;

import java.util.Date;

import androidx.annotation.NonNull;

/**
 * Created by gnut3ll4 on 15/12/15.
 */
public class MonETSNotification implements Comparable<MonETSNotification> {

    private int id;
    private int dossierId;
    private String notificationTexte;
    private Date notificationDateDebutAffichage;
    private String notificationApplicationNom;
    private String url;

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
    public int compareTo(@NonNull MonETSNotification another) {
        if(notificationDateDebutAffichage.after(another.getNotificationDateDebutAffichage())) {
            return -1;
        }
        if(notificationDateDebutAffichage.before(another.getNotificationDateDebutAffichage())) {
            return 1;
        }
        return 0;
    }
}
