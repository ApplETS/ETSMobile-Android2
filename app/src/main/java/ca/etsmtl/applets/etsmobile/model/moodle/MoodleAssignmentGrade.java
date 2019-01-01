package ca.etsmtl.applets.etsmobile.model.moodle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Grade of an assignment
 * <p>
 * Created by Sonphil on 02-09-17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MoodleAssignmentGrade {
    private int id;
    private int assignment;
    private int userid;
    private int attemptnumber;
    private int timecreated;
    private int timemodified;
    private int grader;
    private float grade;

    public int getAssignment() {
        return assignment;
    }

    public float getGrade() {
        return grade;
    }
}
