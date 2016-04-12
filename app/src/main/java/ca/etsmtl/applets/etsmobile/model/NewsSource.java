package ca.etsmtl.applets.etsmobile.model;

import org.json.JSONObject;

/**
 * Created by Steven on 2016-04-11.
 */
public class NewsSource {

    String key;
    String name;
    String type;
    String urlImage;
    String value;

    public NewsSource(JSONObject object){
        try{
            key = object.getString("key");
            name = object.getString("name");
            type = object.getString("type");
            urlImage = object.getString("urlImage");
            value = object.getString("value");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public String getValue() {
        return value;
    }
}
