package ca.etsmtl.applets.etsmobile.model.Moodle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by gnut3ll4 on 10/13/14.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class MoodleCourse {

    private int id;
    private String shortname;
    private String fullname;
    private int enrolledUserCount;
    private String idNumber;
    private int visible;

    public int getId() {
        return id;
    }

    public String getShortname() {
        return shortname;
    }

    public String getFullname() {
        return fullname;
    }

    public int getEnrolledUserCount() {
        return enrolledUserCount;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public int getVisible() {
        return visible;
    }
}
