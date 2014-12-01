package ca.etsmtl.applets.etsmobile.http;

import android.content.Context;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

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
        return getRestTemplate().getForObject(url, Nouvelles.class);
    }
}
