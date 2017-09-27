package ca.etsmtl.applets.etsmobile.model.Moodle;

import com.google.gson.annotations.SerializedName;

/**
 * Submission of an assignment
 * <p>
 * An instance of {@link MoodleAssignmentSubmission} contains an instance of
 * {@link MoodleAssignmentLastAttempt} which gives information about the last attempt.
 * The instance of {@link MoodleAssignmentSubmission} may also contains a non null instance of
 * {@link MoodleAssignmentFeedback}.
 * <p>
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

    /**
     * Assignment feedback
     * <p>
     * Contains an instance of {@link MoodleAssignmentGrade} which gives information about the
     * grade
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
         * @return the student grade rendered into a format suitable for display
         */
        public String getGradeForDisplay() {
            return gradeForDisplay;
        }
    }
}
