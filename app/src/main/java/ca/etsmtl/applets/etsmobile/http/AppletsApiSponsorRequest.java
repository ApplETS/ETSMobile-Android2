package ca.etsmtl.applets.etsmobile.http;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

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

import ca.etsmtl.applets.etsmobile.model.Sponsor;
import ca.etsmtl.applets.etsmobile.model.SponsorList;
import ca.etsmtl.applets.etsmobile.util.HTTPSRequest;
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

        SponsorList sponsorList = null;

        try {

            // Instantiate the custom HttpClient to call Https request
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(address);

            String apiCredentials = context.getString(R.string.credentials_api);

            String basicAuth = "Basic " + new String(new Base64().encode(apiCredentials.getBytes()));
            get.setHeader("Authorization", basicAuth);
            get.setHeader("Content-Type", "application/json; charset=utf-8");
            String method = get.getMethod();

            HttpResponse getResponse = client.execute(get);
            HttpEntity responseEntity = getResponse.getEntity();
            Log.d("code is", "after client.execute");

            String result = EntityUtils.toString(responseEntity, "UTF-8");
            JSONObject data = new JSONObject(result);

            /*JSONObject root = new JSONObject(result);
            JSONObject data = root.getJSONObject("data");*/
            ObjectMapper mapper = new ObjectMapper();
            sponsorList = new SponsorList();
            Log.d("SponsRequest", "json : " + data);
            Iterator keys = data.keys();
            while (keys.hasNext()) {

                int imageResource = 0;
                String currentDynamicKey = (String) keys.next();

                //imageResource = assignResource(currentDynamicKey);

                JSONArray arraySponsors = data.getJSONArray(currentDynamicKey);

                for (int i = 0; i < arraySponsors.length(); i++) {
                    Sponsor sponsor = mapper.readValue(arraySponsors.getJSONObject(i).toString(), Sponsor.class);
                    //sponsor.setImageResource(imageResource);
                    sponsorList.add(sponsor);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return sponsorList;
    }
}
