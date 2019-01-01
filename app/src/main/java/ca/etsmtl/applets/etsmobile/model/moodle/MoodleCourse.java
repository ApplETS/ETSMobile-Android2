package ca.etsmtl.applets.etsmobile.model.moodle;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gnut3ll4 on 10/13/14.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class MoodleCourse {

    public static final int IS_SEMESTER = -1;

    @PrimaryKey
    private int id;
    private String shortname;
    private String fullname;
    @JsonProperty("enrolledusercount")
    @SerializedName("enrolledusercount")
    private int enrolledUserCount;
    @JsonProperty("idnumber")
    @SerializedName("idnumber")
    private String idNumber;
    private int visible;

    public void setCourseSemester(String semester) {
        this.id = IS_SEMESTER;
        this.fullname = semester;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public int getEnrolledUserCount() {
        return enrolledUserCount;
    }

    public void setEnrolledUserCount(int enrolledUserCount) {
        this.enrolledUserCount = enrolledUserCount;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }
}
