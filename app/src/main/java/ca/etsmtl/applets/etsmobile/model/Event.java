package ca.etsmtl.applets.etsmobile.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by laurencedevillers on 2014-10-11.
 */

@DatabaseTable(tableName = "event")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {

    @DatabaseField
    @JsonProperty("event_id")
    String eventID;

    @DatabaseField(id=true)
    @JsonProperty("id")
    String id;

    @DatabaseField
    @JsonProperty("start_date")
    String startDate;

    @DatabaseField
    @JsonProperty("end_date")
    String endDate;

    @DatabaseField
    @JsonProperty("title")
    String title;

    public Event() {
    }

    public Event(String eventID, String id, String startDate, String endDate, String title) {
        this.eventID = eventID;
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
