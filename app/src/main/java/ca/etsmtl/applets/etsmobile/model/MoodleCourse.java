package ca.etsmtl.applets.etsmobile.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by gnut3ll4 on 10/13/14.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class MoodleCourse {

    private int id;
    private String shortname;
    private String fullname;
    private int enrolledUserCount;
    private String idNumber;
    private int visible;

}
