package com.alert.bikeornot.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.zetterstrom.com.forecast.models.DataPoint;
import android.zetterstrom.com.forecast.models.Forecast;

import com.alert.bikeornot.BikeManager;
import com.alert.bikeornot.R;
import com.alert.bikeornot.adapters.DailyForecastAdapter;
import com.alert.bikeornot.models.BikeOrNotResponse;
import com.alert.bikeornot.preferences.Prefs;
import com.alert.bikeornot.utilities.FileUtils;
import com.alert.bikeornot.utilities.PrefUtils;
import com.alert.bikeornot.utilities.TimeUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    @BindView(R.id.layBikeStatus)
    RelativeLayout layBikeStatus;

    @BindView(R.id.layAppBar)
    AppBarLayout layAppBar;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.layCollapsingToolbar)
    CollapsingToolbarLayout layCollapsingToolbar;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.viewOverlay)
    View viewOverlay;

    @BindView(R.id.lblStatusTitle)
    TextView lblStatusTitle;

    @BindView(R.id.lblStatusText)
    TextView lblStatusText;

    @BindView(R.id.lblUpdated)
    TextView lblUpdated;

    private final String TAG = "MainActivity";

    private LinearLayoutManager mLayoutManager;
    private DailyForecastAdapter mAdapter;

    private Forecast forecast;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private final int LOCATION_PERMISSION = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_main, false);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();

        layBikeStatus.setPadding(0, 0, 0, getStatusBarHeight());
        layCollapsingToolbar.setTitle(" ");


    }

    private void GetForecast() {
        forecast = FileUtils.readObjectFromFile(this);
        if (forecast != null) {
            UpdateViews(forecast);
        }
    }

    private void UpdateViews(Forecast forecast) {

        final BikeOrNotResponse response = BikeManager.BikeOrNotHourly(BikeManager.GetTodayDatapoints(forecast));

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if(mAdapter == null) {
            ArrayList<DataPoint> weeklyDatapoints =  BikeManager.GetWeeklyDatapoints(forecast);
            if(weeklyDatapoints.size() > 0) {
                mAdapter = new DailyForecastAdapter(this, weeklyDatapoints);
                mAdapter.setOnItemClickListener(mOnItemClickListener);
                mRecyclerView.setAdapter(mAdapter);
            } else {
                //show bigger refresh button instead of list
                //this will happen if the user as not updated the weather for a week
            }
        }
        else {
            mAdapter.setItems(BikeManager.GetWeeklyDatapoints(forecast));
        }

        viewOverlay.setBackgroundColor(response.getColor());
        layCollapsingToolbar.setContentScrimColor(response.getColor());
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(response.getDarkColor());
        }

        lblStatusTitle.setText(response.getTitle());
        lblStatusText.setText(response.getText());

        long updateTime = PrefUtils.get(MainActivity.this, Prefs.UPDATED_TIME, System.currentTimeMillis());
        lblUpdated.setText(getString(R.string.updated) + " " + TimeUtils.getTimeAgo(updateTime));

        layAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    layCollapsingToolbar.setTitle(response.getTitle());
                    isShow = true;
                } else if(isShow) {
                    layCollapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.menuRefresh:
                FetchWeather();
                break;
            case R.id.menuSettings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void FetchWeather() {
        BikeManager.FetchWeatherApi(this, new Callback<Forecast>() {
            @Override
            public void onResponse(Call<Forecast> call, Response<Forecast> response) {
                long currentTime = System.currentTimeMillis();
                PrefUtils.save(MainActivity.this, Prefs.UPDATED_TIME, currentTime);
                UpdateViews(response.body());
            }

            @Override
            public void onFailure(Call<Forecast> call, Throwable t) {
                Toast.makeText(MainActivity.this, R.string.unknown_error_refresh, Toast.LENGTH_LONG).show();
            }
        });
    }

    private DailyForecastAdapter.OnItemClickListener mOnItemClickListener = new DailyForecastAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View v, DataPoint dataPoint, int position) {

        }
    };


    public int getStatusBarHeight() {
        int result = 0;

        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }

        return result;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {

        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION }, LOCATION_PERMISSION);
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        PrefUtils.save(this, Prefs.LOCATION_LATITUDE, currentLatitude);
        PrefUtils.save(this, Prefs.LOCATION_LONGITUDE, currentLongitude);

        //Only the first one. We don't need to be super accurate.
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        FetchWeather();
    }
}
