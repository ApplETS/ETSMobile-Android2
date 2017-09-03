package ca.etsmtl.applets.etsmobile.model.Moodle;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sonphil on 02-09-17.
 */
public class MoodleAssignmentSubmission {
    @SerializedName("lastattempt")
    private MoodleAssignmentLastAttempt lastAttempt;
    @SerializedName("feedback")
    private MoodleAssignmentFeedback feedback;

    public MoodleAssignmentLastAttempt getLastAttempt() {
        return lastAttempt;
    }

    public MoodleAssignmentFeedback getFeedback() {
        return feedback;
    }

    public class MoodleAssignmentFeedback {
        @SerializedName("grade")
        private MoodleAssignmentGrade grade;
        @SerializedName("gradefordisplay")
        private String gradeForDisplay;

        public MoodleAssignmentGrade getGrade() {
            return grade;
        }

        public String getGradeForDisplay() {
            return gradeForDisplay;
        }
    }
}
