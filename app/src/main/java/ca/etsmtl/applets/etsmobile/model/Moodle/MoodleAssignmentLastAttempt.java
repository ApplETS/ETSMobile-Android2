package ca.etsmtl.applets.etsmobile.model.Moodle;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sonphil on 03-09-17.
 */

public class MoodleAssignmentLastAttempt {

    private static final String STATUS_SUBMITTED = "submitted";

    @SerializedName("submission")
    private Submission submission;
    @SerializedName("teamsubmission")
    private TeamSubmission teamSubmission;
    @SerializedName("submissiongroup")
    private int submissionGroup;
    @SerializedName("submissiongroupmemberswhoneedtosubmit")
    private String[] submissionGroupMemberWhoNeedToSubmit;
    @SerializedName("submissionsenabled")
    private boolean submissionsEnabled;
    private boolean locked;
    private boolean graded;
    @SerializedName("canedit")
    private boolean canEdit;
    @SerializedName("caneditowner")
    private boolean canEditOwner;
    @SerializedName("gradingstatus")
    private String gradingStatus;
    @SerializedName("usergroups")
    private int[] userGroups;

    public Submission getSubmission() {
        return submission;
    }

    public TeamSubmission getTeamSubmission() {
        return teamSubmission;
    }

    public boolean isGraded() {
        return graded;
    }

    public boolean isSubmitted() {
        if (submission != null && submission.status != null)
            return submission.status.equals(STATUS_SUBMITTED);

        return false;
    }

    public boolean isTeamSubmitted() {
        if (teamSubmission != null && teamSubmission.status != null)
            return teamSubmission.status.equals(STATUS_SUBMITTED);

        return false;
    }

    public String getGradingStatus() {
        return gradingStatus;
    }

    public int[] getUserGroups() {
        return userGroups;
    }

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
