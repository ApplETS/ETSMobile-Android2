package ca.etsmtl.applets.etsmobile.model.moodle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by gnut3ll4 on 10/19/14.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class MoodleCoreCourses extends ArrayList<MoodleCoreCourse>{
    public MoodleCoreCourses() {

    }
}
