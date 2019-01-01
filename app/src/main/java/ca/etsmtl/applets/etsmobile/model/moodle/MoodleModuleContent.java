package ca.etsmtl.applets.etsmobile.model.moodle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by gnut3ll4 on 10/19/14.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class MoodleModuleContent {
    private  String type;
    private  String filename;
    private  String filepath;
    private  int filesize;
    private  String fileurl;
    private  String timecreated;
    private  String timemodified;
    private int sortorder;
    private  int userid;
    private  String author;
    private  String license;

    public String getType() {
        return type;
    }

    public String getFilename() {
        return filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public int getFilesize() {
        return filesize;
    }

    public String getFileurl() {
        return fileurl;
    }

    public String getTimecreated() {
        return timecreated;
    }

    public String getTimemodified() {
        return timemodified;
    }

    public int getSortorder() {
        return sortorder;
    }

    public int getUserid() {
        return userid;
    }

    public String getAuthor() {
        return author;
    }

    public String getLicense() {
        return license;
    }
}
