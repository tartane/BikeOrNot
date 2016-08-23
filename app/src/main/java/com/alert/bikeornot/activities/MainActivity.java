package com.alert.bikeornot.activities;

import android.content.Intent;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
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

import java.sql.Time;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.layBikeStatus)
    RelativeLayout layBikeStatus;

    @Bind(R.id.layAppBar)
    AppBarLayout layAppBar;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.layCollapsingToolbar)
    CollapsingToolbarLayout layCollapsingToolbar;

    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @Bind(R.id.viewOverlay)
    View viewOverlay;

    @Bind(R.id.lblStatusTitle)
    TextView lblStatusTitle;

    @Bind(R.id.lblStatusText)
    TextView lblStatusText;

    @Bind(R.id.lblUpdated)
    TextView lblUpdated;

    private LinearLayoutManager mLayoutManager;
    private DailyForecastAdapter mAdapter;

    private Forecast forecast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isConfigured = PrefUtils.get(this, Prefs.IS_CONFIGURED, false);
        if(!isConfigured) {
            /*
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            finish();*/
        }
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        layBikeStatus.setPadding(0, 0, 0, getStatusBarHeight());

        forecast = FileUtils.readObjectFromFile(this);

        layCollapsingToolbar.setTitle(" ");

        if(forecast != null) {
            UpdateViews(forecast);
        }
    }

    private void UpdateViews(Forecast forecast) {
        final BikeOrNotResponse response = BikeManager.BikeOrNotHourly(BikeManager.GetTodayDatapoints(forecast));

        if(mAdapter == null) {
            mAdapter = new DailyForecastAdapter(this, BikeManager.GetWeeklyDatapoints(forecast));
            mAdapter.setOnItemClickListener(mOnItemClickListener);
            mRecyclerView.setAdapter(mAdapter);
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
                break;
            case R.id.menuSettings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
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
}
