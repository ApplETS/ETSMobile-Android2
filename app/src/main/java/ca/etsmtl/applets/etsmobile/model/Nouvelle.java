package ca.etsmtl.applets.etsmobile.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by gnut3ll4 on 11/30/14.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Nouvelle {

    String from;
    String image;
    String title;
    String created_time;
    String facebook_link;
    String updated_time;
    String message;
    String id;
    String icon_link;

    int imageResource;

    public Nouvelle() {}

    public Nouvelle(String from, String image, String title, String created_time, String facebook_link, String updated_time, String message, String id, String icon_link) {
        this.from = from;
        this.image = image;
        this.title = title;
        this.created_time = created_time;
        this.facebook_link = facebook_link;
        this.updated_time = updated_time;
        this.message = message;
        this.id = id;
        this.icon_link = icon_link;
    }

    public String getFrom() {
        return from;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getCreated_time() {
        return created_time;
    }

    public String getFacebook_link() {
        return facebook_link;
    }

    public String getUpdated_time() {
        return updated_time;
    }

    public String getMessage() {
        return message;
    }

    public String getId() {
        return id;
    }

    public String getIcon_link() {
        return icon_link;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }


}



