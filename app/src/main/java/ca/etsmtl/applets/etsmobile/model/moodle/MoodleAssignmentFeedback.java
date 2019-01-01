package ca.etsmtl.applets.etsmobile.model.moodle;

import com.google.gson.annotations.SerializedName;

/**
 * Assignment feedback
 * <p>
 * Contains an instance of {@link MoodleAssignmentGrade} which gives information about the
 * grade
 * <p>
 * Created by Sonphil on 28-12-17.
 */

public class MoodleAssignmentFeedback {
    @SerializedName("grade")
    private MoodleAssignmentGrade grade;
    /**
     * the student grade rendered into a format suitable for display
     */
    @SerializedName("gradefordisplay")
    private String gradeForDisplay;

    public MoodleAssignmentGrade getGrade() {
        return grade;
    }

    /**
     * Returns the student grade rendered into a format suitable for display
     *
     * @return the student grade rendered into a format suitable for display
     */
    public String getGradeForDisplay() {
        return gradeForDisplay;
    }
}
