package ca.etsmtl.applets.etsmobile.model.Moodle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by gnut3ll4 on 10/16/14.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class MoodleToken {

    private String token;

    public MoodleToken() {
        token = "";
    }

    public MoodleToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
