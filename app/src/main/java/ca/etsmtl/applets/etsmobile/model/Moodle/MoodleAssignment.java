package ca.etsmtl.applets.etsmobile.model.Moodle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    public String getName() {
        return name;
    }

    public long getDueDate() {
        return dueDate;
    }
}
