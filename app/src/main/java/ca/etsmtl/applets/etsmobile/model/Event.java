package ca.etsmtl.applets.etsmobile.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by laurencedevillers on 2014-10-11.
 */

@DatabaseTable(tableName = "event")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Event implements IHoraireRows{

    @DatabaseField(id=true)
    @SerializedName("id")
    String id;

    @DatabaseField
    @SerializedName("start_date")
    String startDate;

    @DatabaseField
    @SerializedName("end_date")
    String endDate;

    @DatabaseField
    @SerializedName("summary")
    String title;

    public Event() {
    }

    public Event(String id, String startDate, String endDate, String title) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDateDebut() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getDateFin() {
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
