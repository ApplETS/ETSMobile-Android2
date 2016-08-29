package ca.etsmtl.applets.etsmobile.http;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kobjects.base64.Base64;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        // Instantiate the custom HttpClient to call Https request

        String apiCredentials = context.getString(R.string.credentials_api);
        String basicAuth = "Basic " + Base64.encode(apiCredentials.getBytes());

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("content-type", "multipart/form-data; boundary=---011000010111000001101001")
                .addHeader("authorization", basicAuth)
                .build();

        Response response = client.newCall(request).execute();

        JSONObject result = new JSONObject(response.body().string());
        // If the returned value of "data" returned by the API becomes an Array,
        // change the type of JSONObject to JSONArray
        JSONArray data = result.getJSONObject("data").getJSONArray("ets");

        String s = data.toString();

        EventList eventList = new Gson().fromJson(s, EventList.class);

        return eventList;
    }
}