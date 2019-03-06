package ca.etsmtl.applets.etsmobile.http;

import android.content.Context;

import com.google.gson.Gson;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import ca.etsmtl.applets.etsmobile.model.EventList;
import ca.etsmtl.applets.etsmobile2.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


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

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Response response = client.newCall(request).execute();

        String calendarJson = response.body().string();

        return new Gson().fromJson(calendarJson, EventList.class);
    }
}