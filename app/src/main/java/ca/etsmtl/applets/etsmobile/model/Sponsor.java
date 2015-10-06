package ca.etsmtl.applets.etsmobile.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Steven on 2015-09-30.
 */
public class Sponsor {
    @JsonProperty("index")
    int index;
    @JsonProperty("name")
    String name;
    @JsonProperty("image_url")
    String image_url;

    public Sponsor() {
    }

    public Sponsor(int index, String image_url, String name) {
        this.index = index;
        this.image_url = image_url;
        this.name = name;

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
