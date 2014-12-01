package ca.etsmtl.applets.etsmobile.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by gnut3ll4 on 11/30/14.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Nouvelles {

    ArrayList<Nouvelle> data;

    public Nouvelles() {}


    public Nouvelles(ArrayList<Nouvelle> nouvelles) {
        this.data = nouvelles;
    }

    public ArrayList<Nouvelle> getNouvelles() {
        return data;
    }

    public void setNouvelles(ArrayList<Nouvelle> nouvelles) {
        this.data = nouvelles;
    }
}
