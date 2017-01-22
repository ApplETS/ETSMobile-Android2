package ca.etsmtl.applets.etsmobile.model.applets_events;


public class SourceEvenement {


    private String name;
    private String type;
    private String urlImage;
    private String key;
    private String value;

    public SourceEvenement(String name, String type, String urlImage, String key, String value) {
        this.name = name;
        this.type = type;
        this.urlImage = urlImage;
        this.key = key;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
