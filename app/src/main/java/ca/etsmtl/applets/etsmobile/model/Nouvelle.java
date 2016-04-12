package ca.etsmtl.applets.etsmobile.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.json.JSONObject;

/**
 * Created by gnut3ll4 on 11/30/14.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Nouvelle {

    String date;
    String id;
    String id_source;
    String link;
    String message;
    String titre;
    String urlPicture;


    public Nouvelle() {}

    public Nouvelle(JSONObject object) {
        try{
            date = object.getString("date");
            id = object.getString("id");
            id_source = object.getString("id_source");
            link = object.getString("link");
            message = object.getString("message");
            titre = object.getString("titre");
            urlPicture = object.getString("urlPicture");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public String getDate() {return date;}

    public String getId_source() {return id_source;}

    public String getLink() {return link;}

    public String getTitre() {return titre;}

    public String getUrlPicture() {return urlPicture; }

    public String getMessage() {
        return message;
    }

    public String getId() {
        return id;
    }

}



