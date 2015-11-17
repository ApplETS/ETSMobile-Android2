package ca.etsmtl.applets.etsmobile.model;

/**
 * Created by gnut3ll4 on 18/05/15.
 */
public class Apps {

    private String packageName;
    private String imageResourceId;
    private String appName;

    public Apps(String packageName, String imageResourceId, String appName) {

        this.packageName = packageName;
        this.imageResourceId = imageResourceId;
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getImageResourceId() {
        return imageResourceId;
    }
}
