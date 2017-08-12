package ca.etsmtl.applets.etsmobile.model.Moodle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Sonphil on 12-08-17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MoodleAssignmentCourse {
    @JsonProperty("id")
    private int id;
    @JsonProperty("fullname")
    private String fullName;
    @JsonProperty("shortname")
    private String shortName;
    @JsonProperty("timemodified")
    private int timeModified;
    @JsonProperty("assignments")
    private List<MoodleAssignment> assignments;

    public String getFullName() {
        return fullName;
    }

    public List<MoodleAssignment> getAssignments() {
        return assignments;
    }
}
