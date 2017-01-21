package ca.etsmtl.applets.etsmobile.http;

import android.content.Context;

import com.google.gson.Gson;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import ca.etsmtl.applets.etsmobile.model.applets_events.EvenementCommunaute;
import ca.etsmtl.applets.etsmobile.model.applets_events.EvenementCommunauteList;
import ca.etsmtl.applets.etsmobile.model.applets_events.SourceEvenement;
import ca.etsmtl.applets.etsmobile.model.applets_events.SourceEvenementList;
import ca.etsmtl.applets.etsmobile2.R;

public class AppletsApiEvenementsRequest extends SpringAndroidSpiceRequest<EvenementCommunauteList> {

    private Context context;
    private SourceEvenement source;

    public AppletsApiEvenementsRequest(Context context, SourceEvenement source) {
        super(EvenementCommunauteList.class);
        this.context = context;
        this.source = source;
    }

    @Override
    public EvenementCommunauteList loadDataFromNetwork() throws Exception {

        String eventsAddress = context.getString(R.string.applets_api_events, source.getKey());

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(eventsAddress)
                .get()
                .build();

        Response response = client.newCall(request).execute();

        String result = response.body().string();
        EvenementCommunauteList evenementList = new Gson().fromJson(result, EvenementCommunauteList.class);

        for(EvenementCommunaute event : evenementList) {
            event.setSourceEvenement(source);
        }

        return evenementList;
    }

    /**
     * This method generates a unique cache key for this request. In this case
     * our cache key depends just on the keyword.
     * @return
     */
    public String createCacheKey() {
        return "events_"+source.getKey();
    }
}
