package ca.etsmtl.applets.etsmobile.model.Moodle;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import ca.etsmtl.applets.etsmobile.util.Utility;

/**
 * Created by Sonphil on 12-08-17.
 */

public class MoodleAssignment {
    private int id;
    private int cmid;
    private int course;
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
    @SerializedName("timemodified")
    private int timeModified;
    @SerializedName("completionsubmit")
    private int completionSubmit;
    @SerializedName("cutofdate")
    private int cutOfDate;
    @SerializedName("teamsubmission")
    private int teamSubmission;
    @SerializedName("requireallteammemberssubmit")
    private int requireAllTeamMembersSubmit;
    @SerializedName("teamsubmissiongroupingid")
    private int teamSubmissionGroupingId;
    @SerializedName("maxattempts")
    private int maxAttempts;
    private String intro;

    public int getId() {
        return id;
    }

    public int getCmid() {
        return cmid;
    }

    public String getName() {
        return name;
    }

    public int getGrade() {
        return grade;
    }

    /**
     * Returns a {@link Date} instance representing the due date of the assignment
     *
     * @return A {@link Date} instance representing the due date of the assignment
     */
    public Date getDueDateObj() {
        Date date = Utility.getDateTimeFromUnixTime(dueDate);

        return date;
    }

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

    public String getIntro() {
        return intro;
    }
}
