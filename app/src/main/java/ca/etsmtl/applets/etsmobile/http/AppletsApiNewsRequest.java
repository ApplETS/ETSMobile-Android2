package ca.etsmtl.applets.etsmobile.http;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kobjects.base64.Base64;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Iterator;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import ca.etsmtl.applets.etsmobile.model.Nouvelle;
import ca.etsmtl.applets.etsmobile.model.Nouvelles;
import ca.etsmtl.applets.etsmobile.util.HTTPSRequest;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by gnut3ll4 on 11/30/14.
 */
public class AppletsApiNewsRequest extends SpringAndroidSpiceRequest<Nouvelles> {

    private Context context;
    private String source;
    private String startDate;
    private String endDate;

    public AppletsApiNewsRequest(Context context, String source, String startDate, String endDate) {
        super(Nouvelles.class);
        this.context = context;
        this.source = source;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String convertStreamToString(InputStream inputStream) {
        BufferedReader buffReader = new BufferedReader(new InputStreamReader(
                inputStream));
        StringBuilder stringBuilder = new StringBuilder();

        String line = null;
        try {
            while ((line = buffReader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public Nouvelles loadDataFromNetwork() throws Exception {

//        String address = context.getString(R.string.applets_api_news, source, startDate, endDate);
        String address = context.getString(R.string.applets_api_news_all);
        address = "https://api.clubapplets.ca/news/all";

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

        URL url = new URL(address);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();*/

        // Load CAs from an InputStream
    // (could be from a resource or ByteArrayInputStream or ...)
        try {

            // Instantiate the custom HttpClient
           /* DefaultHttpClient client = new HTTPSRequest(context);
            HttpGet get = new HttpGet("https://api.clubapplets.ca/news/all");

            String userCredentials = context.getString(R.string.credentials_api);
            String basicAuth = "Basic " + new String(new Base64().encode(userCredentials.getBytes()));
            get.setHeader("Authorization", basicAuth);
            get.setHeader("Content-Type", "application/json; charset=utf-8");
            String method = get.getMethod();

            HttpResponse getResponse = client.execute(get);
            HttpEntity responseEntity = getResponse.getEntity();*/

            ///////////////////////////////
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            InputStream caInput = new BufferedInputStream(context.getResources().openRawResource(R.raw.applets_https_certificate));
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            SSLContext contextt = SSLContext.getInstance("TLS");
            contextt.init(null, tmf.getTrustManagers(), null);

            // Tell the URLConnection to use a SocketFactory from our SSLContext
            URL url = new URL(address);
            HttpsURLConnection con =
                    (HttpsURLConnection) url.openConnection();
            con.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });
            con.setSSLSocketFactory(contextt.getSocketFactory());


            String userCredentials = context.getString(R.string.credentials_api);
            String basicAuth = "Basic " + new String(new Base64().encode(userCredentials.getBytes()));
            con.setRequestProperty("Authorization", basicAuth);
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            con.setUseCaches(false);

            // Get the response code
            int statusCode = con.getResponseCode();

            InputStream is = null;

            if (statusCode >= 200 && statusCode < 400) {
                // Create an InputStream in order to extract the response object
                is = con.getInputStream();
            }
            else {
                is = con.getErrorStream();
            }

            String response = convertStreamToString(is);
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
//        urlConnection.disconnect();
String result = "";
        JSONObject root = new JSONObject(result);
        JSONObject data = root.getJSONObject("data");
        ObjectMapper mapper = new ObjectMapper();
        Nouvelles nouvelles = new Nouvelles();

        Iterator keys = data.keys();
        while (keys.hasNext()) {

            int imageResource = 0;
            String currentDynamicKey = (String) keys.next();

            imageResource = assignResource(currentDynamicKey);

            JSONArray arrayNews = data.getJSONArray(currentDynamicKey);

            for (int i = 0; i < arrayNews.length(); i++) {
                Nouvelle nouvelle = mapper.readValue(arrayNews.getJSONObject(i).toString(), Nouvelle.class);
                nouvelle.setImageResource(imageResource);
                nouvelles.add(nouvelle);
            }
        }

        return nouvelles;
    }


    private int assignResource(String key) {
        switch (key) {
            case "ets":
                return R.drawable.ic_ets;

            case "substance":
                return R.drawable.ic_substance;

            case "centresportif":
                return R.drawable.ic_centresportif;

            case "applets":
                return R.drawable.ic_applets;

            case "esports":
                return R.drawable.ic_esports;

            case "rafale":
                return R.drawable.ic_rafale;

            case "rockanddance":
                return R.drawable.ic_rockanddance;

            case "conjure":
                return R.drawable.ic_conjure;

            case "rockets":
                return R.drawable.ic_rockets;

            case "phoenix":
                return R.drawable.ic_phoenix;

            case "avioncargo":
                return R.drawable.ic_ace;

            case "clubcycliste":
                return R.drawable.ic_clubcycliste;

            case "football":
                return R.drawable.ic_football;

            case "ingenieuses":
                return R.drawable.ic_ingenieuses;

            case "debatpiranha":
                return R.drawable.ic_debatpiranha;

            case "radiopiranha":
                return R.drawable.ic_radiopiranha;

            case "walkingmachine":
                return R.drawable.ic_walkingmachine;

            case "atlhetsiques":
                return R.drawable.ic_athletsiques;

            case "aeets":
                return R.drawable.ic_aeets;

            case "rugby":
                return R.drawable.ic_rugby;

            case "bibliotheque":
                return R.drawable.ic_bibliotheque;

            case "capra":
                return R.drawable.ic_capra;

            case "ieee":
                return R.drawable.ic_ieee;

            case "pontpop":
                return R.drawable.ic_pontpop;

            case "omer":
                return R.drawable.ic_omer;

            case "baja":
                return R.drawable.ic_baja;

            case "canoedebeton":
                return R.drawable.ic_canoedebeton;

            case "chinook":
                return R.drawable.ic_chinook;

            case "sonia":
                return R.drawable.ic_sonia;

            case "lanets":
                return R.drawable.ic_lanets;

            case "formuleets":
                return R.drawable.ic_formuleets;

            case "eclipse":
                return R.drawable.ic_eclipse;

            case "turbulence":
                return R.drawable.ic_turbulence;

            case "preci":
                return R.drawable.ic_preci;

            case "reflets":
                return R.drawable.ic_reflets;

            case "crabeets":
                return R.drawable.ic_crabeets;

            case "decliq":
                return R.drawable.ic_decliq;

            case "quiets":
                return R.drawable.ic_quiets;

            case "dronolab":
                return R.drawable.ic_dronolab;

            case "liets":
                return R.drawable.ic_liets;

            case "radiosansgenie":
                return R.drawable.ic_radiosansgenie;

            case "coopets":
                return R.drawable.ic_coopets;

            case "integrale":
                return R.drawable.ic_integrale;

            case "geniale":
                return R.drawable.ic_geniale;
            default:
                return 0;

        }
    }
}
