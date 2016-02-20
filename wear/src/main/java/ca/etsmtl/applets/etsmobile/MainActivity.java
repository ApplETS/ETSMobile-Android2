package ca.etsmtl.applets.etsmobile;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends WearableActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener{

    TextView mClock, mText;
    ListView listView;
    GoogleApiClient googleClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mClock = (TextView) findViewById(R.id.clock);
        mText = (TextView) findViewById(R.id.list);


        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        /*IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);*/
    }

    @Override
    public void onResume(){
        super.onResume();
        googleClient.connect();
        Log.d("wearMainAct", "Request sent");
    }

    /*public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            Log.v("wearMainAct", "Main activity received message: " + message);
            String value = message + " GB";
            // Display message in UI
            mText.setText(value);
        }
    }*/
    @Override
    public void onDataChanged(DataEventBuffer dataEvents)
    {
        Log.v("onDataChanged", "entered successfully" );
        for (DataEvent event : dataEvents)
        {
            Log.v("onDataChanged", "entered for with " + event.getType() );

            DataItem item = event.getDataItem();
            DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
            if(!dataMap.get("map").equals("nothing to show!")) {
                String itemPath = item.getUri().getPath();
                Seances seances = new Seances();
                seances.getData(dataMap);
                mText.setText(seances.libelleCours);
            }else{
                mText.setText(dataMap.get("map").toString());
            }
        }
    }

    class SendToDataLayerThread extends Thread {
        String path;
        String message;

        // Constructor to send a message to the data layer
        SendToDataLayerThread(String p, String msg) {
            path = p;
            message = msg;
        }

        public void run() {
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
            for (Node node : nodes.getNodes()) {
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleClient, node.getId(), path, message.getBytes()).await();
                if (result.getStatus().isSuccess()) {
                    Log.v("wearThread", "Message: {" + message + "} sent to: " + node.getDisplayName());
                } else {
                    // Log an error
                    Log.v("wearThread", "ERROR: failed to send Message");
                }
            }
        }
    }
    @Override
    public void onConnected(Bundle connectionHint) {
        Wearable.DataApi.addListener(googleClient, this);
        new SendToDataLayerThread("/today_req", "value_requested").start();
    }

    @Override
    public void onConnectionSuspended(int cause) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) { }

    @Override
    protected void onStop() {
        if (null != googleClient && googleClient.isConnected()) {
            googleClient.disconnect();
        }
        Wearable.DataApi.removeListener(googleClient, this);
        super.onStop();
    }
}
