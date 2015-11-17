package ca.etsmtl.applets.etsmobile.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Steven on 2015-09-30.
 */
@DatabaseTable(tableName = "sponsor")
public class Sponsor {

    @DatabaseField
    @JsonProperty("url")
    String url;
    @DatabaseField
    @JsonProperty("index")
    int index;
    @DatabaseField(id = true)
    @JsonProperty("name")
    String name;
    @DatabaseField
    @JsonProperty("image_url")
    String image_url;

    public Sponsor() {
    }

    public Sponsor(String url, int index, String image_url, String name) {
        this.url = url;
        this.index = index;
        this.image_url = image_url;
        this.name = name;

    }

    public String getUrl() {
        return url;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return image_url;
    }


}
