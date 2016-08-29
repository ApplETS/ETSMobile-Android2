package ca.etsmtl.applets.etsmobile.http;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kobjects.base64.Base64;

import java.util.Iterator;

import ca.etsmtl.applets.etsmobile.model.Sponsor;
import ca.etsmtl.applets.etsmobile.model.SponsorList;
import ca.etsmtl.applets.etsmobile2.R;

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
