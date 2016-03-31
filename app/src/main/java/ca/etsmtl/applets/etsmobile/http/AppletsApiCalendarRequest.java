package ca.etsmtl.applets.etsmobile.http;

import android.content.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kobjects.base64.Base64;
import java.util.Iterator;
import ca.etsmtl.applets.etsmobile.model.Event;
import ca.etsmtl.applets.etsmobile.model.EventList;
import ca.etsmtl.applets.etsmobile2.R;


public class AppletsApiCalendarRequest extends SpringAndroidSpiceRequest<EventList> {

    private Context context;
    private String startDate = "";
    private String endDate = "";

    public AppletsApiCalendarRequest(Context context, String startDate, String endDate) {
        super(EventList.class);
        this.context = context;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public EventList loadDataFromNetwork() throws Exception {

        String url = context.getString(R.string.applets_api_calendar, "ets", startDate, endDate);
        EventList eventList = new EventList();

        try {
            // Instantiate the custom HttpClient to call Https request
            // @TODO - Use OkHttp instead of DefaultHttpClient (which is deprecated)
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(url);

            String apiCredentials = context.getString(R.string.credentials_api);

            String basicAuth = "Basic " + new String(new Base64().encode(apiCredentials.getBytes()));
            get.setHeader("Authorization", basicAuth);
            get.setHeader("Content-Type", "application/json; charset=utf-8");
            String method = get.getMethod();

            HttpResponse getResponse = client.execute(get);
            HttpEntity responseEntity = getResponse.getEntity();

            JSONObject result = new JSONObject(EntityUtils.toString(responseEntity, "UTF-8"));
            // If the returned value of "data" returned by the API becomes an Array,
            // change the type of JSONObject to JSONArray
            JSONObject data = result.getJSONObject("data");

            // The API might eventually return other JSONArray or JSONObject
            // You get them here with the key of the Array/Object
            JSONArray ets = data.getJSONArray("ets");
            for(int i=0; i<ets.length();i++){
                JSONObject contents = ets.getJSONObject((i));
                Event event = new Event(contents.getString("id"),
                        contents.getString("start_date"), contents.getString("end_date"),
                        contents.getString("summary"));
                eventList.add(event);
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        return eventList;
    }
}