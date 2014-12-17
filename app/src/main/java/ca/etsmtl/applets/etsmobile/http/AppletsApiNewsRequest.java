package ca.etsmtl.applets.etsmobile.http;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import ca.etsmtl.applets.etsmobile.model.Nouvelle;
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

    public AppletsApiNewsRequest(Context context, String source, String startDate, String endDate) {
        super(Nouvelles.class);
        this.context = context;
        this.source = source;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public Nouvelles loadDataFromNetwork() throws Exception {

        String url = context.getString(R.string.applets_api_news, source, startDate, endDate);

        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();

        String result = IOUtils.toString(urlConnection.getInputStream());
        urlConnection.disconnect();

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
                return R.drawable.ic_launcher;

            case "substance":
                return R.drawable.ic_launcher;

            case "centresportif":
                return R.drawable.ic_launcher;

            case "applets":
                return R.drawable.ic_launcher;

            case "esports":
                return R.drawable.ic_launcher;

            case "rafale":
                return R.drawable.ic_launcher;

            case "rockanddance":
                return R.drawable.ic_launcher;

            case "conjure":
                return R.drawable.ic_launcher;

            case "rockets":
                return R.drawable.ic_courriel;

            case "phoenix":
                return R.drawable.ic_launcher;

            case "avioncargo":
                return R.drawable.ic_launcher;

            case "clubcycliste":
                return R.drawable.ic_launcher;

            case "football":
                return R.drawable.ic_launcher;

            case "ingenieuses":
                return R.drawable.ic_launcher;

            case "debatpiranha":
                return R.drawable.ic_launcher;

            case "radiopiranha":
                return R.drawable.ic_launcher;

            case "walkingmachine":
                return R.drawable.ic_launcher;

            case "atlhetsiques":
                return R.drawable.ic_launcher;

            case "aeets":
                return R.drawable.ic_launcher;

            case "rugby":
                return R.drawable.ic_launcher;

            case "bibliotheque":
                return R.drawable.ic_launcher;

            case "capra":
                return R.drawable.ic_launcher;

            case "ieee":
                return R.drawable.ic_launcher;

            case "pontpop":
                return R.drawable.ic_launcher;

            case "omer":
                return R.drawable.ic_launcher;

            case "baja":
                return R.drawable.ic_launcher;

            case "canoedebeton":
                return R.drawable.ic_launcher;

            case "chinook":
                return R.drawable.ic_launcher;

            case "sonia":
                return R.drawable.ic_launcher;

            case "lanets":
                return R.drawable.ic_launcher;

            case "formuleets":
                return R.drawable.ic_launcher;

            case "eclipse":
                return R.drawable.ic_launcher;

            case "turbulence":
                return R.drawable.ic_launcher;

            case "preci":
                return R.drawable.ic_launcher;

            case "reflets":
                return R.drawable.ic_launcher;

            case "crabeets":
                return R.drawable.ic_launcher;

            case "decliq":
                return R.drawable.ic_launcher;

            case "quiets":
                return R.drawable.ic_launcher;

            case "dronolab":
                return R.drawable.ic_launcher;

            case "liets":
                return R.drawable.ic_launcher;

            case "radiosansgenie":
                return R.drawable.ic_launcher;

            case "coopets":
                return R.drawable.ic_launcher;

            case "integrale":
                return R.drawable.ic_launcher;

            case "geniale":
                return R.drawable.ic_launcher;
            default:
                return 0;

        }
    }
}
