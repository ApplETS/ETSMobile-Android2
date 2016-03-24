package ca.etsmtl.applets.etsmobile.model;

import org.json.JSONObject;

/**
 * Created by steve on 2016-03-23.
 */
public class ConsommationBandePassante {

    String date;
    double download, upload;
    int idChambre;

    public ConsommationBandePassante(JSONObject jsonObject){
        try{
            date = jsonObject.getString("date");
            download = jsonObject.getDouble("download");
            idChambre = jsonObject.getInt("idChambre");
            upload = jsonObject.getDouble("upload");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public String getDate() {
        return date;
    }

    public double getDownload() {
        return download;
    }

    public double getUpload() {
        return upload;
    }

    public int getIdChambre() {
        return idChambre;
    }
}
