package com.bikeornot;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.bikeornot.preferences.Prefs;
import com.bikeornot.utilities.PrefUtils;
import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.data.FreezableUtils;
import com.mobvoi.android.wearable.DataEvent;
import com.mobvoi.android.wearable.DataEventBuffer;
import com.mobvoi.android.wearable.DataMapItem;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Node;
import com.mobvoi.android.wearable.Wearable;
import com.mobvoi.android.wearable.WearableListenerService;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class DataLayerListenerService extends WearableListenerService {

    private static final String TAG = "DataLayer";
    private static final String BIKE_PATH = "/bike";
    private MobvoiApiClient mMobvoiApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mMobvoiApiClient = new MobvoiApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mMobvoiApiClient.connect();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "onDataChanged: " + dataEvents);

        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        dataEvents.close();
        ConnectionResult connectionResult = mMobvoiApiClient
                .blockingConnect(30, TimeUnit.SECONDS);
        if (!connectionResult.isSuccess()) {
            return;
        }

        // Loop through the events and send a message back to the node that created the data item.
        for (DataEvent event : events) {
            Uri uri = event.getDataItem().getUri();
            String path = uri.getPath();
            Toast.makeText(this, path, Toast.LENGTH_LONG).show();

            if (BIKE_PATH.equals(path)) {
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                int color = dataMapItem.getDataMap().getInt("color");
                int darkColor = dataMapItem.getDataMap().getInt("darkColor");
                String title = dataMapItem.getDataMap().getString("title");
                String text = dataMapItem.getDataMap().getString("text");
                String status = dataMapItem.getDataMap().getString("status");

                PrefUtils.save(this, Prefs.COLOR, color);
                PrefUtils.save(this, Prefs.DARK_COLOR, darkColor);
                PrefUtils.save(this, Prefs.TITLE, title);
                PrefUtils.save(this, Prefs.TEXT, text);
                PrefUtils.save(this, Prefs.STATUS, status);
            }
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "onMessageReceived: " + messageEvent);
        // Check to see if the message is to start an activity
        switch (messageEvent.getPath()) {
        }
    }

    @Override
    public void onPeerConnected(Node peer) {
        Log.d(TAG, "onPeerConnected: " + peer);
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        Log.d(TAG, "onPeerDisconnected: " + peer);
    }
}
