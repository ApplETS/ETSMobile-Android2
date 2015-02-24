package ca.etsmtl.applets.etsmobile.http;

import android.content.Context;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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


        TrustManager[] trustAllCerts = new TrustManager[] {
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

        return getRestTemplate().getForObject(url, EventList.class);
    }
}
