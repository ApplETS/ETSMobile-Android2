package ca.etsmtl.applets.etsmobile.http;

import android.content.Context;

import com.google.gson.Gson;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import ca.etsmtl.applets.etsmobile.model.applets_events.SourceEvenementList;
import ca.etsmtl.applets.etsmobile2.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AppletsApiSourcesRequest extends SpringAndroidSpiceRequest<SourceEvenementList> {

    private Context context;

    public AppletsApiSourcesRequest(Context context) {
        super(SourceEvenementList.class);
        this.context = context;
    }

    @Override
    public SourceEvenementList loadDataFromNetwork() throws Exception {

        String sourceAddress = context.getString(R.string.applets_api_events_sources);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(sourceAddress)
                .get()
                .build();

        Response response = client.newCall(request).execute();

        String result = response.body().string();
        SourceEvenementList sources = new Gson().fromJson(result, SourceEvenementList.class);

        return sources;
    }

    /**
     * This method generates a unique cache key for this request. In this case
     * our cache key depends just on the keyword.
     * @return
     */
    public String createCacheKey() {
        return "eventsources";
    }
}
