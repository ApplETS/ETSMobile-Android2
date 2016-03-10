package ca.etsmtl.applets.etsmobile.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

import ca.etsmtl.applets.etsmobile.db.DatabaseHelper;
import ca.etsmtl.applets.etsmobile.model.Seances;
import ca.etsmtl.applets.etsmobile.util.SeanceComparator;

public class WearService extends WearableListenerService implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    GoogleApiClient googleClient;
    private DateTime dateTime;
    private DatabaseHelper databaseHelper = null;
    private ArrayList<Seances> todayList = new ArrayList<Seances>();

    @Override
    public void onCreate(){
        super.onCreate();

        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleClient.connect();
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        Log.v("mobListenerService", "Message path received on phone is: " + messageEvent.getPath());
        if (messageEvent.getPath().equals("/today_req")) {
            final String message = new String(messageEvent.getData());
            Log.v("mobListenerService", "Message received on phone is: " + message);

            // Broadcast message to wearable activity for display
            try{
                dateTime = new DateTime();

                DateTime.Property pDoW = dateTime.dayOfWeek();
                DateTime.Property pDoM = dateTime.dayOfMonth();
                DateTime.Property pMoY = dateTime.monthOfYear();
                databaseHelper = new DatabaseHelper(this);
                SimpleDateFormat seancesFormatter = new SimpleDateFormat("yyyy-MM-dd", getResources().getConfiguration().locale);
                todayList = (ArrayList<Seances>) databaseHelper.getDao(Seances.class).queryBuilder().where().like("dateDebut", seancesFormatter.format(dateTime.toDate()).toString() + "%").query();
                Collections.sort(todayList, new SeanceComparator());
            }catch(Exception e){
                e.printStackTrace();
            }

            if(todayList != null && todayList.size()>0) {
                for (int i = 0; i < todayList.size(); i++) {
                    Seances seances;
                    seances = todayList.get(i);
                    new SendToDataLayerThread("/today_req", seances).start();
                }
            }
            /*new SendToDataLayerThread("/today_req", todayList).start();
            Intent messageIntent = new Intent();
            messageIntent.setAction(Intent.ACTION_SEND);
            messageIntent.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);*/
        }
        else {
            super.onMessageReceived(messageEvent);
        }
    }


    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            Log.v("mobListenerReceiv", "Mobile Listener received message: " + message);
        }
    }

    class SendToDataLayerThread extends Thread {
        String path;
        //ArrayList<Seances> list = new ArrayList<Seances>();
        Seances seances;

        // Constructor to send a message to the data layer
        SendToDataLayerThread(String p, Seances seances) {
            path = p;
            this.seances = seances;
        }

        public void run() {
            PutDataMapRequest dataMapReq = PutDataMapRequest.create(path);
            DataMap dataMap = dataMapReq.getDataMap();
            //list.add(new Seances().getData(new DataMap().putString("libelleCours", "ELE462")));

            /*if(list != null && list.size()>0) {
                for(int i=0; i< list.size(); i++ ) {
                    dataMap.putDataMap("map", list.get(i).putData());
                    dataMap.putString("time",String.valueOf(dateTime.getMillisOfSecond()));
                }
            }else{
                dataMap.putString("map", "nothing to show!");
                //dataMapReq.getDataMap().putDataMap("contents", dataMap);
            }*/
            dataMap.putDataMap("map", seances.putData());
            dataMap.putString("time",String.valueOf(dateTime.getMillisOfSecond()));

            PutDataRequest request = dataMapReq.asPutDataRequest();
            Log.v("mobListener", "datamap = " + dataMap.toString());
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
            for (Node node : nodes.getNodes()) {
                DataApi.DataItemResult dataItemResult = Wearable.DataApi
                        .putDataItem(googleClient, request).await();
                if (dataItemResult.getStatus().isSuccess()) {
                    Log.v("mobSendData", "Data sent to: " + node.getDisplayName()+ " successfully!!");
                } else {
                    // Log an error
                    Log.v("mobSendData", "ERROR: failed to send Message");
                }
            }
        }
    }
}
