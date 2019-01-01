package ca.etsmtl.applets.etsmobile.service;


import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.ArrayList;

import ca.etsmtl.applets.etsmobile.Seances;

public class ListenerService extends WearableListenerService {


    @Override
    public void onCreate() {
        super.onCreate();
    }

    private static final String TAG = ListenerService.class.getSimpleName();

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        Log.d(TAG, "onDataChanged: " + dataEvents);

        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED
                    && event.getDataItem() != null
                    && event.getDataItem().getUri().getPath().equals("/today_req")) {

                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                ArrayList<DataMap> seancesDataMapList = dataMapItem.getDataMap().getDataMapArrayList("list_seances");

                ArrayList<Seances> seances = new ArrayList<>();

                for (DataMap seanceDataMap : seancesDataMapList) {
                    Seances seance = new Seances();
                    seance.getData(seanceDataMap);
                    seances.add(seance);
                }

                Intent intent = new Intent("seances_update");
                intent.putExtra("seances", seances);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }
        }
        super.onDataChanged(dataEvents);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.v(TAG, "onMessageReceived: " + messageEvent);
    }

}
