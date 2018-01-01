package ca.etsmtl.applets.etsmobile.model.Moodle;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Course that contains a list of Moodle assignments that the user can view
 * <p>
 * Created by Sonphil on 12-08-17.
 */
@Entity
@TypeConverters(MoodleAssignmentCourse.MoodleAssignmentConverters.class)
public class MoodleAssignmentCourse {
    @PrimaryKey
    private int id;
    @SerializedName("fullname")
    private String fullName;
    @SerializedName("shortname")
    private String shortName;
    @SerializedName("timemodified")
    private int timeModified;
    @SerializedName("assignments")
    private List<MoodleAssignment> assignments;

    public MoodleAssignmentCourse(int id, String fullName, String shortName, int timeModified, List<MoodleAssignment> assignments) {
        this.id = id;
        this.fullName = fullName;
        this.shortName = shortName;
        this.timeModified = timeModified;
        this.assignments = assignments;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public int getTimeModified() {
        return timeModified;
    }

    public void setTimeModified(int timeModified) {
        this.timeModified = timeModified;
    }

    public List<MoodleAssignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<MoodleAssignment> assignments) {
        this.assignments = assignments;
    }

    /**
     * Convert assignment to strings so that they can be stored in the DB
     * Can also convert the strings back to the assignment
     */
    public final static class MoodleAssignmentConverters {
        @TypeConverter
        public static List<MoodleAssignment> stringToAssignmentCourses(String json) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<MoodleAssignment>>() {}.getType();
            return gson.fromJson(json, type);
        }

        @TypeConverter
        public static String assignmentCoursesToString(List<MoodleAssignment> list) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<MoodleAssignment>>() {}.getType();
            return gson.toJson(list, type);
        }
    }
}
