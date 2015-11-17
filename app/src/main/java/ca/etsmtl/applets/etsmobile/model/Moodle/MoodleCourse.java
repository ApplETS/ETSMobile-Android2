package ca.etsmtl.applets.etsmobile.model.Moodle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by gnut3ll4 on 10/13/14.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class MoodleCourse {

    public static final int IS_SEMESTER = -1;

    private int id;
    private String shortname;
    private String fullname;
    private int enrolledUserCount;
    private String idNumber;
    private int visible;

    public void setCourseSemester(String semester) {
        this.id = IS_SEMESTER;
        this.fullname = semester;
    }

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
