package ca.etsmtl.applets.etsmobile.model.moodle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by gnut3ll4 on 10/19/14.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class MoodleCoreModule {
    private int id;
    private String url;
    private  String name;
    private int visible;
    private String modicon;
    private String modname;
    private String modplural;
    private int indent;

    private int position;

    private ArrayList<MoodleModuleContent> contents;

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public int getVisible() {
        return visible;
    }

    public String getModicon() {
        return modicon;
    }

    public String getModname() {
        return modname;
    }

    public String getModplural() {
        return modplural;
    }

    public int getIndent() {
        return indent;
    }

    public ArrayList<MoodleModuleContent> getContents() {
        return contents;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

}
