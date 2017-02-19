package ca.etsmtl.applets.etsmobile.service;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import org.joda.time.DateTime;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ca.etsmtl.applets.etsmobile.db.DatabaseHelper;
import ca.etsmtl.applets.etsmobile.model.Seances;
import ca.etsmtl.applets.etsmobile.util.Constants;
import ca.etsmtl.applets.etsmobile.util.SeanceComparator;
import ca.etsmtl.applets.etsmobile2.BuildConfig;

public class WearService extends WearableListenerService {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        if (android.os.Debug.isDebuggerConnected()) {
            android.os.Debug.waitForDebugger();
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        if (messageEvent.getPath().equals("/today_req")) {

            final String message = new String(messageEvent.getData());

            List<Seances> seances = new ArrayList<>();

            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            SimpleDateFormat seancesFormatter = new SimpleDateFormat("yyyy-MM-dd", getResources().getConfiguration().locale);

            try {
                seances = databaseHelper.getDao(Seances.class).queryBuilder()
                        .where()
                        .like("dateDebut", seancesFormatter.format(DateTime.now().toDate()) + "%")
                        .query();
                Collections.sort(seances, new SeanceComparator());
            } catch (SQLException e) {
                e.printStackTrace();
            }

            new SendToDataLayerThread("/today_req", seances, this).start();

        }
    }

    class SendToDataLayerThread extends Thread {
        private final Context context;
        String path;
        List<Seances> seances;

        SendToDataLayerThread(String path, List<Seances> seances, Context context) {
            this.path = path;
            this.seances = seances;
            this.context = context;
        }

        public void run() {
            GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(Wearable.API)
                    .build();

            ConnectionResult connectionResult = googleApiClient.blockingConnect(
                    Constants.GOOGLE_API_CLIENT_TIMEOUT_S, TimeUnit.SECONDS);

            if (!connectionResult.isSuccess() || !googleApiClient.isConnected()) {
                Log.e("ETSMobile-Wear", connectionResult.getErrorMessage());
                return;
            }

            PutDataMapRequest dataMapReq = PutDataMapRequest.create(path);

            ArrayList<DataMap> dataMapArrayList = new ArrayList<>();
            for (Seances seance : seances) {
                dataMapArrayList.add(seance.putData());
            }
            dataMapReq.getDataMap().putDataMapArrayList("list_seances", dataMapArrayList);

            PutDataRequest request = dataMapReq.asPutDataRequest();

            DataApi.DataItemResult dataItemResult = Wearable
                    .DataApi
                    .putDataItem(googleApiClient, request)
                    .await();

            if (dataItemResult.getStatus().isSuccess()) {
                if (BuildConfig.DEBUG) Log.d("SendToDataLayerThread", "Data sent successfully!!");
            } else {
                // Log an error
                if (BuildConfig.DEBUG)
                    Log.d("SendToDataLayerThread", "ERROR: failed to send Message");
            }
        }
    }
}
