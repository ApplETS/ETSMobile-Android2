package ca.etsmtl.applets.etsmobile.model.Moodle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import ca.etsmtl.applets.etsmobile.util.Utility;

/**
 * Created by Sonphil on 12-08-17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class MoodleAssignment {
    @JsonProperty("id")
    private int id;
    @JsonProperty("cmid")
    private int cmid;
    @JsonProperty("course")
    private int course;
    @JsonProperty("name")
    private String name;
    @JsonProperty("nosubmissions")
    private int noSubmissions;
    @JsonProperty("submissiondrafts")
    private int submissionDrafts;
    @JsonProperty("duedate")
    private long dueDate;
    private Date dueDateObj;
    @JsonProperty("allowsubmissionsfromdate")
    private int allowSubmissionsFromDate;
    @JsonProperty("grade")
    private int grade;
    @JsonProperty("timemodified")
    private int timeModified;
    @JsonProperty("completionsubmit")
    private int completionSubmit;
    @JsonProperty("cutofdate")
    private int cutOffDate;
    @JsonProperty("teamsubmission")
    private int teamSubmission;
    @JsonProperty("requireallteammemberssubmit")
    private int requireAllTeamMembersSubmit;
    @JsonProperty("teamsubmissiongroupingid")
    private int teamSubmissionGroupingId;
    @JsonProperty("maxattempts")
    private int maxAttempts;
    @JsonProperty("intro")
    private String intro;

    public String getName() {
        return name;
    }

    public Date getDueDateObj() {
        Date date = Utility.getDateTimeFromUnixTime(dueDate);

        return date;
    }
}
