package ca.etsmtl.applets.etsmobile.model.Moodle;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sonphil on 12-08-17.
 */
public class MoodleAssignmentCourse {
    private int id;
    @SerializedName("fullname")
    private String fullName;
    @SerializedName("shortname")
    private String shortName;
    @SerializedName("timemodified")
    private int timeModified;
    @SerializedName("assignments")
    private List<MoodleAssignment> assignments;

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public List<MoodleAssignment> getAssignments() {
        return assignments;
    }
}
