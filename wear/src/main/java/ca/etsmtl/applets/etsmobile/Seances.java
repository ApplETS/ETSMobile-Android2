package ca.etsmtl.applets.etsmobile;

import com.google.android.gms.wearable.DataMap;

/**
 * Created by Steven on 2016-02-20.
 */
public class Seances {

    public String dateDebut;

    public String dateFin;

    public String coursGroupe;

    public String nomActivite;

    public String local;

    public String descriptionActivite;

    public String libelleCours;

    public Seances ()
    {
    }
    public void getData(DataMap map){
        dateDebut = map.getString("dateDebut");
        dateFin = map.getString("dateFin");
        coursGroupe = map.getString("coursGroupe");
        nomActivite = map.getString("nomActivite");
        local = map.getString("local");
        descriptionActivite = map.getString("descriptionActivite");
        libelleCours = map.getString("libelleCours");
    }
}
