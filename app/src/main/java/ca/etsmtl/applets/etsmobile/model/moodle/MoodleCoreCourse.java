package ca.etsmtl.applets.etsmobile.model.moodle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by gnut3ll4 on 10/19/14.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class MoodleCoreCourse {


    private int id;
    private String name;
    private int visible;
    private String summary;
    private int summaryFormat;
    private ArrayList<MoodleCoreModule> modules;


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getVisible() {
        return visible;
    }

    public String getSummary() {
        return summary;
    }

    public int getSummaryFormat() {
        return summaryFormat;
    }

    public ArrayList<MoodleCoreModule> getModules() {
        return modules;
    }
}
