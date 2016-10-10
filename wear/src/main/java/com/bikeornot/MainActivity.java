package com.bikeornot;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bikeornot.preferences.Prefs;
import com.bikeornot.utilities.PrefUtils;
import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.data.FreezableUtils;
import com.mobvoi.android.wearable.DataApi;
import com.mobvoi.android.wearable.DataEvent;
import com.mobvoi.android.wearable.DataEventBuffer;
import com.mobvoi.android.wearable.Wearable;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends Activity implements MobvoiApiClient.ConnectionCallbacks, MobvoiApiClient.OnConnectionFailedListener, DataApi.DataListener {
    private static final String TAG = "MainActivity";

    private MobvoiApiClient mMobvoiApiClient;

    ImageView imgBikeStatus;
    TextView lblStatusTitle;
    TextView lblStatusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                lblStatusText = (TextView) stub.findViewById(R.id.lblStatusText);
                lblStatusTitle = (TextView) stub.findViewById(R.id.lblStatusTitle);
                imgBikeStatus = (ImageView) stub.findViewById(R.id.imgBikeStatus);

                String title = PrefUtils.get(MainActivity.this, Prefs.TITLE, "");
                String text = PrefUtils.get(MainActivity.this, Prefs.TEXT, "");
                int color = PrefUtils.get(MainActivity.this, Prefs.COLOR, 0);
                int darkColor = PrefUtils.get(MainActivity.this, Prefs.DARK_COLOR, 0);
                String status = PrefUtils.get(MainActivity.this, Prefs.STATUS, "");

                lblStatusTitle.setText(title);
                lblStatusText.setText(text);
                stub.setBackgroundColor(color);

            }
        });

        mMobvoiApiClient = new MobvoiApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMobvoiApiClient.connect();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Suspended", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        for(DataEvent event: events) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {

            }
        }
    }
}
