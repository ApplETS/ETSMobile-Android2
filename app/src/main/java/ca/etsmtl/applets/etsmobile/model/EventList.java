package ca.etsmtl.applets.etsmobile.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by laurencedevillers on 2014-10-11.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class EventList extends ArrayList<Event>{

    @SerializedName("ets")
    ArrayList<Event> events;

    public EventList() {
    }

    public EventList(ArrayList<Event> events) {
        this.events = events;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }
}