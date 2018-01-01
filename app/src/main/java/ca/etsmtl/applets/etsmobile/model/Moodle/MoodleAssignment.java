package ca.etsmtl.applets.etsmobile.model.Moodle;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import ca.etsmtl.applets.etsmobile.util.Utility;

/**
 * Created by Sonphil on 12-08-17.
 */

public class MoodleAssignment {
    /**
     * Assignment id
     **/
    private int id;
    /**
     * Course module id
     **/
    private int cmid;
    /**
     * Course id
     **/
    private int course;
    /**
     * Assignment name
     **/
    private String name;
    @SerializedName("nosubmissions")
    private int noSubmissions;
    @SerializedName("submissiondrafts")
    private int submissionDrafts;
    @SerializedName("duedate")
    private long dueDate;
    private Date dueDateObj;
    @SerializedName("allowsubmissionsfromdate")
    private int allowSubmissionsFromDate;
    private int grade;
    /**
     * Last time assignment was modified
     **/
    @SerializedName("timemodified")
    private int timeModified;
    /**
     * If enabled, set activity as complete following submission
     **/
    @SerializedName("completionsubmit")
    private int completionSubmit;
    /**
     * Date after which submission is not accepted without an extension
     **/
    @SerializedName("cutofdate")
    private int cutOfDate;
    /**
     * If enabled, students submit as a team
     **/
    @SerializedName("teamsubmission")
    private int teamSubmission;
    /**
     * If enabled, all team members must submit
     **/
    @SerializedName("requireallteammemberssubmit")
    private int requireAllTeamMembersSubmit;
    /**
     * The grouping id for the team submission groups
     **/
    @SerializedName("teamsubmissiongroupingid")
    private int teamSubmissionGroupingId;
    /**
     * If enabled, hide identities until reveal identities actioned
     * True if student identities is hidden from graders
     **/
    @SerializedName("blindmarking")
    private int blindMarking;
    /**
     * Show identities for a blind marking assignment
     **/
    @SerializedName("revealidentities")
    private int revealIdentities;
    /**
     * Method used to control opening new attempts
     **/
    @SerializedName("attemptreopenmethod")
    private String attemptReopenMethod;
    /**
     * Prevent submission if user is not in a group
     **/
    @SerializedName("preventsubmissionnotingroup")
    private int preventSubmissionNotIngroup;
    /**
     * Maximum number of attempts allowed
     **/
    @SerializedName("maxattempts")
    private int maxAttempts;
    /**
     * Enable marking workflow
     **/
    @SerializedName("markingworkflow")
    private int markingWorkflow;
    /**
     * Student must accept submission statement
     **/
    @SerializedName("requiresubmissionstatement")
    private int requireSubmissionStatement;
    /**
     * Assignment intro, not always returned because it deppends on the activity configuration
     **/
    private String intro;

    /**
     * Returns the assignment id
     *
     * @return assignment id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the course module id
     *
     * @return course module id
     */
    public int getCmid() {
        return cmid;
    }

    /**
     * Returns the assignment name
     *
     * @return assignment name
     */
    public String getName() {
        return name;
    }

    public int getGrade() {
        return grade;
    }

    /**
     * Returns true if a due date has been set for the assignment
     *
     * @return true if a due date has been set for the assignment
     */
    public boolean isDueDateSet() {
        return dueDate > 0;
    }

    /**
     * Returns a {@link Date} instance representing the due date of the assignment
     * <p>
     * If the due date hasn't been set, an {@link Date} instance set to December 31th 1969 19:00:00
     * will be returned.
     *
     * @return A {@link Date} instance representing the due date of the assignment
     */
    public Date getDueDateObj() {
        Date date = Utility.getDateTimeFromUnixTime(dueDate);

        return date;
    }

    /**
     * Set the assignment name
     *
     * @param name the assignment name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the assignment's due date
     *
     * @param dueDate Unix time in seconds
     */
    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Return true if students submit as a team
     *
     * @return true if students submit as a team
     */
    public boolean isTeamSubmission() {
        return teamSubmission == 1;
    }

    /**
     * Return true if student must accept submission statement
     *
     * @return true if student must accept submission statement
     */
    public boolean isRequireAllTeamMembersSubmit() {
        return requireAllTeamMembersSubmit == 1;
    }

    public boolean isPreventSubmissionNotIngroup() {
        return preventSubmissionNotIngroup == 1;
    }

    /**
     * Return the maximum number of attempts allowed
     *
     * @return maximum number of attempts allowed
     */
    public int getMaxAttempts() {
        return maxAttempts;
    }

    public String getIntro() {
        return intro;
    }
}
