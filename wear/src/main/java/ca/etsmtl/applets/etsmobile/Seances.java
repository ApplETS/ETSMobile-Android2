package ca.etsmtl.applets.etsmobile;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.wearable.DataMap;

public class Seances implements Parcelable {

    String dateDebut;
    String dateFin;
    String coursGroupe;
    String nomActivite;
    String local;
    String descriptionActivite;
    String libelleCours;

    public Seances() {
    }

    public void getData(DataMap map) {
        dateDebut = map.getString("dateDebut");
        dateFin = map.getString("dateFin");
        coursGroupe = map.getString("coursGroupe");
        nomActivite = map.getString("nomActivite");
        local = map.getString("local");
        descriptionActivite = map.getString("descriptionActivite");
        libelleCours = map.getString("libelleCours");
    }

    // Parcelling part
    public Seances(Parcel in) {
        String[] data = new String[7];
        in.readStringArray(data);
        int i = 0;
        this.dateDebut = data[i++];
        this.dateFin = data[i++];
        this.coursGroupe = data[i++];
        this.nomActivite = data[i++];
        this.local = data[i++];
        this.descriptionActivite = data[i++];
        this.libelleCours = data[i];

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        String[] params = {
                this.dateDebut,
                this.dateFin,
                this.coursGroupe,
                this.nomActivite,
                this.local,
                this.descriptionActivite,
                this.libelleCours};
        dest.writeStringArray(params);
    }

    public static final Parcelable.Creator<Seances> CREATOR =
            new Parcelable.Creator<Seances>() {
                @Override
                public Seances createFromParcel(Parcel in) {
                    return new Seances(in);
                }

                @Override
                public Seances[] newArray(int size) {
                    return new Seances[size];
                }
            };
}
