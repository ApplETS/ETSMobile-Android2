package ca.etsmtl.applets.etsmobile.model.moodle;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Last submission attempt of an assignment
 * <p>
 * {@link Submission} and {@link TeamSubmission} indicate the submission status of the assignment.
 * If the assignment is not a team assignment, the {@link TeamSubmission} is null.
 * <p>
 * Created by Sonphil on 03-09-17.
 */

public class MoodleAssignmentLastAttempt {

    private static final String STATUS_SUBMITTED = "submitted";

    @SerializedName("submission")
    private Submission submission;
    @SerializedName("teamsubmission")
    private TeamSubmission teamSubmission;
    /**
     * The submission group id
     **/
    @SerializedName("submissiongroup")
    private int submissionGroup;
    /**
     * List of users who still need to submit (for group submissions only)
     **/
    @SerializedName("submissiongroupmemberswhoneedtosubmit")
    private String[] submissionGroupMemberWhoNeedToSubmit;
    /**
     * True if submissions are enabled
     **/
    @SerializedName("submissionsenabled")
    private boolean submissionsEnabled;
    /**
     * True if new submissions are locked
     **/
    private boolean locked;
    /**
     * True if the submission is graded
     **/
    private boolean graded;
    /**
     * True if the user can edit the current submission
     **/
    @SerializedName("canedit")
    private boolean canEdit;
    /**
     * True if the owner of the submission can edit it
     **/
    @SerializedName("caneditowner")
    private boolean canEditOwner;
    /**
     * True if the user can submit
     **/
    @SerializedName("cansubmit")
    private boolean canSubmit;
    /**
     * True if blind marking is enabled for this assignment
     **/
    @SerializedName("blindmarking")
    private boolean blindMarking;
    @SerializedName("gradingstatus")
    private String gradingStatus;
    /**
     * User groups in the course
     **/
    @SerializedName("usergroups")
    private int[] userGroups;

    public Submission getSubmission() {
        return submission;
    }

    public TeamSubmission getTeamSubmission() {
        return teamSubmission;
    }

    /**
     * @return true if the submission is graded
     */
    public boolean isGraded() {
        return graded;
    }

    /**
     * Return true if the assignment has been submitted
     *
     * @return true if the assignment has been submitted
     */
    public boolean isSubmitted() {
        if (submission != null && submission.status != null)
            return submission.status.equals(STATUS_SUBMITTED);

        return false;
    }

    /**
     * @return true if the assignment has been submitted by the team
     */
    public boolean isTeamSubmitted() {
        if (teamSubmission != null && teamSubmission.status != null)
            return teamSubmission.status.equals(STATUS_SUBMITTED);

        return false;
    }

    /**
     * @param isTeamSubmission true if the assignment must be submitted as a team
     * @return true if the assignment has been submitted or team submitted
     */
    private boolean isSubmittedOrTeamSubmitted(boolean isTeamSubmission) {
        return isTeamSubmission ? isTeamSubmitted() : isSubmitted();
    }

    /**
     * @return grading status
     */
    public String getGradingStatus() {
        return gradingStatus;
    }

    @DrawableRes
    public int getSubmissionIconRes(boolean isTeamSubmission, Date dueDate) {
        if (isSubmittedOrTeamSubmitted(isTeamSubmission))
            return R.drawable.ic_assignment_turned_in_black_24dp;
        else {
            if (dueDate.getTime() > 0 && dueDate.before(new Date())) {
                // Si la date de remise a été paramétrée et que celle-ci a été dépassée...
                return R.drawable.ic_assignment_late_black_24dp;
            } else {
                // Sinon, la date de remise n'a pas encore été dépassée
                return R.drawable.ic_assignment_black_24dp;
            }
        }
    }

    @ColorRes
    public int getSubmissionColorRes(boolean isTeamSubmission, Date dueDate) {
        if (isSubmittedOrTeamSubmitted(isTeamSubmission))
            return R.color.success_color;
        else {
            if (dueDate.getTime() > 0 && dueDate.before(new Date())) {
                // Si la date de remise a été paramétrée et que celle-ci a été dépassée...
                return R.color.failure_color;
            } else {
                // Sinon, la date de remise n'a pas encore été dépassée
                return R.color.black;
            }
        }
    }

    @StringRes
    public int getSubmissionStatusRes(boolean isTeamSubmission, Date dueDate) {
        if (isSubmittedOrTeamSubmitted(isTeamSubmission))
            return R.string.moodle_assignment_status_submitted;
        else {
            if (dueDate.getTime() > 0 && dueDate.before(new Date())) {
                // Si la date de remise a été paramétrée et que celle-ci a été dépassée...
                return R.string.moodle_assignment_status_late;
            } else {
                // Sinon, la date de remise n'a pas encore été dépassée
                return R.string.moodle_assignment_status_not_submitted;
            }
        }
    }

    /**
     * Submission of an assignment
     */
    public class Submission {
        private int id;
        private int userid;
        @SerializedName("attemptnumber")
        private int attemptNumber;
        @SerializedName("timecreated")
        private int timeCreated;
        @SerializedName("timmodified")
        private int timeModified;
        private String status;
        private int groupid;
        private int assignment;
        private int latest;
    }

    /**
     * A team submission of an assignment
     */
    public class TeamSubmission {
        private int id;
        private int userid;
        @SerializedName("attemptnumber")
        private int attemptNumber;
        @SerializedName("timecreated")
        private int timeCreated;
        @SerializedName("timmodified")
        private int timeModified;
        private String status;
        private int groupid;
        private int assignment;
        private int latest;
    }
}
