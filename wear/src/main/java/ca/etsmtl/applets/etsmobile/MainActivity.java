package ca.etsmtl.applets.etsmobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ca.etsmtl.applets.etsmobile.utils.Utils;

public class MainActivity extends WearableActivity {

    SeancesPagerAdapter adapter;
    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    BroadcastReceiver broadcastReceiver;
    private GridViewPager pager;
    private ImageView imageViewNoCourses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        pager = (GridViewPager) findViewById(R.id.pager);

        imageViewNoCourses = (ImageView) findViewById(R.id.imageview_no_courses);

        adapter = new SeancesPagerAdapter(MainActivity.this, new ArrayList<Seances>());

        pager.setAdapter(adapter);

        DotsPageIndicator dotsPageIndicator = (DotsPageIndicator) findViewById(R.id.page_indicator);
        dotsPageIndicator.setPager(pager);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ArrayList<Seances> seances = intent.getParcelableArrayListExtra("seances");

                loadSeances(seances);

            }
        };

    }

    @Override
    public void onResume() {
        super.onResume();

        new FetchDataAsyncTask(this).execute();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(broadcastReceiver, new IntentFilter("seances_update"));


        new SendMessageAsyncTask(this).execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(broadcastReceiver);
    }

    public void loadSeances(List<Seances> seances) {
        for (int i = 0; i < seances.size(); i++) {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
            DateTime seanceDateFin = formatter.parseDateTime(seances.get(i).dateFin);
            if (DateTime.now().isAfter(seanceDateFin)) {
                seances.remove(i);
            }
        }

        adapter.setSeances(seances);

        if (seances.size() == 0) {
            imageViewNoCourses.setVisibility(View.VISIBLE);
        } else {
            imageViewNoCourses.setVisibility(View.GONE);
        }
    }


    /**
     * Used to notify ETSMobile on the cellphone that we want to receive seances
     */
    private class SendMessageAsyncTask extends
            AsyncTask<Void, Void, Void> {

        private Context mContext;

        SendMessageAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected Void doInBackground(Void... params) {

            GoogleApiClient googleApiClient = new GoogleApiClient.Builder(mContext)
                    .addApi(Wearable.API)
                    .build();

            ConnectionResult connectionResult = googleApiClient.blockingConnect(
                    Constants.GOOGLE_API_CLIENT_TIMEOUT_S, TimeUnit.SECONDS);

            if (!connectionResult.isSuccess() || !googleApiClient.isConnected()) {
                Log.e("ETSMobile-Wear", connectionResult.getErrorMessage());
                return null;
            }

            MessageApi.SendMessageResult result =
                    Wearable.MessageApi.sendMessage(
                            googleApiClient,
                            Utils.getRemoteNodeId(googleApiClient),
                            "/today_req",
                            null)
                            .await();

            if (result.getStatus().isSuccess()) {
                Log.d("wearThread", "SUCCESS : Message sent");
            } else {
                Log.d("wearThread", "ERROR: failed to send Message");
            }

            return null;

        }

    }

    // This is used to access the data layer api and retrieve previously stored data
    private class FetchDataAsyncTask extends
            AsyncTask<Void, Void, ArrayList<Seances>> {

        private Context mContext;

        public FetchDataAsyncTask(Context context) {
            mContext = context;
        }

        private String getLocalNodeId(GoogleApiClient googleApiClient) {
            NodeApi.GetLocalNodeResult nodeResult = Wearable.NodeApi.getLocalNode(googleApiClient).await();
            return nodeResult.getNode().getId();
        }

        private String getRemoteNodeId(GoogleApiClient googleApiClient) {
            NodeApi.GetConnectedNodesResult nodesResult =
                    Wearable.NodeApi.getConnectedNodes(googleApiClient).await();
            List<Node> nodes = nodesResult.getNodes();
            if (nodes.size() > 0) {
                return nodes.get(0).getId();
            }
            return null;
        }

        @Override
        protected ArrayList<Seances> doInBackground(Void... params) {

            ArrayList<Seances> seances = new ArrayList<>();

            // Connect to Play Services and the Wearable API
            GoogleApiClient googleApiClient = new GoogleApiClient.Builder(mContext)
                    .addApi(Wearable.API)
                    .build();

            ConnectionResult connectionResult = googleApiClient.blockingConnect(
                    Constants.GOOGLE_API_CLIENT_TIMEOUT_S, TimeUnit.SECONDS);

            if (!connectionResult.isSuccess() || !googleApiClient.isConnected()) {
                Log.e("ETSMobile-Wear", connectionResult.getErrorMessage());
                return null;
            }

//            Uri uri = params[0];

            Uri uri = new Uri.Builder()
                    .scheme(PutDataRequest.WEAR_URI_SCHEME)
                    .authority(getRemoteNodeId(googleApiClient))
                    .path("/today_req")
                    .build();

            // /today_req
            DataApi.DataItemResult dataItemResult = Wearable.DataApi
                    .getDataItem(googleApiClient, uri).await();

            if (dataItemResult.getStatus().isSuccess() && dataItemResult.getDataItem() != null) {
                DataMapItem dataMapItem = DataMapItem.fromDataItem(dataItemResult.getDataItem());

                try {
                    List<DataMap> listSeancesDataMap = new ArrayList<>(
                            dataMapItem.getDataMap()
                                    .getDataMapArrayList("list_seances")
                    );

                    // Loop through each attraction, adding them to the list
                    for (DataMap seanceDataMap : listSeancesDataMap) {
                        Seances seance = new Seances();
                        seance.getData(seanceDataMap);
                        seances.add(seance);
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            return seances;
        }

        @Override
        protected void onPostExecute(ArrayList<Seances> seances) {

            if (seances != null && seances.size() > 0) {
                // Update UI based on the result of the background processing
                Log.d("FetchDataAsyncTask", "result:" + seances);
                loadSeances(seances);
            } else {
                Log.e("FetchDataAsyncTask", "No seances returned");
            }
        }
    }

    //*/


}
