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
        EventList eventList = null;

        try {
            // Instantiate the custom HttpClient to call Https request
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(url);

            String apiCredentials = context.getString(R.string.credentials_api);

            String basicAuth = "Basic " + new String(new Base64().encode(apiCredentials.getBytes()));
            get.setHeader("Authorization", basicAuth);
            get.setHeader("Content-Type", "application/json; charset=utf-8");
            String method = get.getMethod();

            HttpResponse getResponse = client.execute(get);
            HttpEntity responseEntity = getResponse.getEntity();

            String result = EntityUtils.toString(responseEntity, "UTF-8");
            JSONObject data = new JSONObject(result);

            ObjectMapper mapper = new ObjectMapper();
            eventList = new EventList();
            Iterator keys = data.keys();


            while (keys.hasNext()) {
                String currentDynamicKey = (String) keys.next();
                JSONArray arrayEvents = data.getJSONArray(currentDynamicKey);

                for(int i = 0; i < arrayEvents.length(); i++){
                    Event event = mapper.readValue(arrayEvents.getJSONObject(i).toString(), Event.class);
                    eventList.add(event);
                }
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        return eventList;

        /*TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                    }

                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                }
        };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

        return getRestTemplate().getForObject(url, EventList.class);*/
    }
}