package ca.etsmtl.applets.etsmobile.http;

import android.content.Context;

import com.google.gson.Gson;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import ca.etsmtl.applets.etsmobile.model.applets_events.SourceEvenementList;
import ca.etsmtl.applets.etsmobile2.R;

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
}
