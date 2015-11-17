package ca.etsmtl.applets.etsmobile.model.Moodle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by gnut3ll4 on 10/13/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MoodleProfile {



    private String sitename;
    private String username;
    private String firstname;
    private String lastname;
    private String fullname;
    private String lang;

    @JsonProperty("userid")
    private int userId;

    @JsonProperty("siteurl")
    private String siteUrl;

    @JsonProperty("userpictureurl")
    private String userPictureUrl;

    private String exception;
    private String errorcode;
    private String message;

    public MoodleProfile() {

    }


    public String getSitename() {
        return sitename;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFullname() {
        return fullname;
    }

    public String getLang() {
        return lang;
    }

    public int getUserId() {
        return userId;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public String getUserPictureUrl() {
        return userPictureUrl;
    }

    public String getException() {
        return exception;
    }

    public String getErrorcode() {
        return errorcode;
    }

    public String getMessage() {
        return message;
    }
}
