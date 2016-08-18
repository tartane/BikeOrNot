package com.alert.bikeornot.activities;

import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
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
import com.alert.bikeornot.enums.EBikeOrNot;
import com.alert.bikeornot.models.BikeOrNotResponse;
import com.alert.bikeornot.preferences.Prefs;
import com.alert.bikeornot.utilities.FileUtils;
import com.alert.bikeornot.utilities.PrefUtils;

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

    @Bind(R.id.lblStatus)
    TextView lblStatus;

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
        layCollapsingToolbar.setTitle(" ");
        layAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    layCollapsingToolbar.setTitle("Bike today!");
                    isShow = true;
                } else if(isShow) {
                    layCollapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });

        forecast = FileUtils.readObjectFromFile(this);

        if(forecast != null) {
            mAdapter = new DailyForecastAdapter(this, forecast.getDaily().getDataPoints());
            mAdapter.setOnItemClickListener(mOnItemClickListener);
            mRecyclerView.setAdapter(mAdapter);

            BikeOrNotResponse response = BikeManager.BikeOrNotHourly(BikeManager.GetTodayDatapoints(forecast));

            viewOverlay.setBackgroundColor(response.getColor());
            layCollapsingToolbar.setContentScrimColor(response.getColor());
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(response.getDarkColor());
            }

            lblStatus.setText(response.getTitle());


        }
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
                        Forecast forecast = response.body();
                        mAdapter.setItems(forecast.getDaily().getDataPoints());

                    }

                    @Override
                    public void onFailure(Call<Forecast> call, Throwable t) {
                        Toast.makeText(MainActivity.this, R.string.unknown_error_refresh, Toast.LENGTH_LONG).show();

                    }
                });
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
