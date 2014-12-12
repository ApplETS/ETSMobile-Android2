package ca.etsmtl.applets.etsmobile.http;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import ca.etsmtl.applets.etsmobile.model.Nouvelle;
import ca.etsmtl.applets.etsmobile.model.Nouvelles;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by gnut3ll4 on 11/30/14.
 */
public class AppletsApiNewsRequest extends SpringAndroidSpiceRequest<Nouvelles> {

    private Context context;
    private String source;
    private String startDate;
    private String endDate;

    public AppletsApiNewsRequest(Context context,String source , String startDate, String endDate) {
        super(Nouvelles.class);
        this.context = context;
        this.source = source;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public Nouvelles loadDataFromNetwork() throws Exception {

        String url = context.getString(R.string.applets_api_news, source, startDate, endDate);

        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();

        String result = IOUtils.toString(urlConnection.getInputStream());
        urlConnection.disconnect();

        JSONObject root = new JSONObject(result);
        JSONObject data = root.getJSONObject("data");
        ObjectMapper mapper = new ObjectMapper();
        Nouvelles nouvelles = new Nouvelles();

        Iterator keys = data.keys();
        while(keys.hasNext()) {
            String currentDynamicKey = (String)keys.next();

            JSONArray arrayNews = data.getJSONArray(currentDynamicKey);

            for(int i = 0 ; i < arrayNews.length() ; i++) {
                nouvelles.add(mapper.readValue(arrayNews.getJSONObject(i).toString(), Nouvelle.class));
            }
        }

        return nouvelles;
    }
}
