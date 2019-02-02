package ca.etsmtl.applets.etsmobile.http;

import android.content.Context;

import com.google.gson.Gson;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;


import ca.etsmtl.applets.etsmobile.model.SponsorList;
import ca.etsmtl.applets.etsmobile2.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AppletsApiSponsorRequest extends SpringAndroidSpiceRequest<SponsorList> {

    private Context context;

    public AppletsApiSponsorRequest(Context context) {
        super(SponsorList.class);
        this.context = context;
    }

    @Override
    public SponsorList loadDataFromNetwork() throws Exception {

        String address = context.getString(R.string.applets_api_sponsors);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(address)
                .get()
                .build();

        Response response = client.newCall(request).execute();

        String result = response.body().string();
        SponsorList sponsors = new Gson().fromJson(result, SponsorList.class);

        return sponsors;
    }
}
