package ca.etsmtl.applets.etsmobile.model.Moodle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Sonphil on 12-08-17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MoodleAssignmentCourses {
    @JsonProperty("courses")
    private List<MoodleAssignmentCourse> courses;

    public List<MoodleAssignmentCourse> getCourses() {
        return courses;
    }
}
