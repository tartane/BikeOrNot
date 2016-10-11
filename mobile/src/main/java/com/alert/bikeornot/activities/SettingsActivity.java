package com.alert.bikeornot.activities;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.zetterstrom.com.forecast.models.Forecast;

import com.alert.bikeornot.BikeManager;
import com.alert.bikeornot.BikeService;
import com.alert.bikeornot.BootReceiver;
import com.alert.bikeornot.R;
import com.alert.bikeornot.adapters.SettingsListAdapter;
import com.alert.bikeornot.dialogs.LocationDialogFragment;
import com.alert.bikeornot.dialogs.TimePickerDialogFragment;
import com.alert.bikeornot.preferences.PrefItem;
import com.alert.bikeornot.preferences.Prefs;
import com.alert.bikeornot.utilities.PrefUtils;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String MODE_FIRST_TIME = "mode_first_time";
    private ArrayList<Object> mPrefItems = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;
    private final String DIALOG_HOME_LOCATION = "dialog_home_location";
    private final String DIALOG_WORK_LOCATION = "dialog_work_location";
    private final String DIALOG_TIME_PICKER = "dialog_time_picker";
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @Bind(R.id.btnSave)
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getExtras();
        String mode = null;
        if(bundle != null) {
            mode = getIntent().getExtras().getString(MODE_FIRST_TIME);
        }
        if(mode == null || mode.equals("")){
            getSupportActionBar().setTitle(R.string.settings);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            btnSave.setVisibility(View.GONE);
        } else {
            getSupportActionBar().setTitle(R.string.first_time_setup);
            btnSave.setVisibility(View.VISIBLE);
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Validate the required prefs.
                    String startLocation = PrefUtils.get(SettingsActivity.this, Prefs.START_LOCATION, null);
                    String notificationTime = PrefUtils.get(SettingsActivity.this, Prefs.NOTIFICATION_TIME, null);

                    if(startLocation == null) {
                        Toast.makeText(SettingsActivity.this, "Home/Start Location is required", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(notificationTime == null) {
                        Toast.makeText(SettingsActivity.this, "Notification time is required", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(!notificationTime.equals("-1")) {
                        int hour = Integer.valueOf(notificationTime.split(":")[0]);
                        int minute = Integer.valueOf(notificationTime.split(":")[1]);
                        BootReceiver.scheduleAlarms(SettingsActivity.this, BootReceiver.shouldTriggerNextDay(hour, minute));
                    }

                    BikeManager.FetchWeatherApi(SettingsActivity.this, new Callback<Forecast>() {
                        @Override
                        public void onResponse(Call<Forecast> call, Response<Forecast> response) {
                            long currentTime = System.currentTimeMillis();
                            PrefUtils.save(SettingsActivity.this, Prefs.UPDATED_TIME, currentTime);
                            PrefUtils.save(SettingsActivity.this, Prefs.IS_CONFIGURED, true);
                            startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                        }

                        @Override
                        public void onFailure(Call<Forecast> call, Throwable t) {
                            Toast.makeText(SettingsActivity.this, R.string.unknown_error_refresh, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }

        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(mLayoutManager);

        PrefUtils.getPrefs(this).registerOnSharedPreferenceChangeListener(this);

        refreshItems();

        mRecyclerView.setAdapter(new SettingsListAdapter(mPrefItems));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshItems() {
        mPrefItems = new ArrayList<>();
        mPrefItems.add(getString(R.string.location));
        mPrefItems.add(new PrefItem(this, R.drawable.ic_home_black_24dp, R.string.home_start_location, Prefs.START_LOCATION, "", true,
                new PrefItem.OnClickListener() {
                    @Override
                    public void onClick(final PrefItem item) {
                        LocationDialogFragment locationDialogFragment = new LocationDialogFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(LocationDialogFragment.TITLE_ARG, item.getTitle());
                        LatLng oldLatLng = PrefGpsToLatLng(item);
                        if(oldLatLng != null)
                            bundle.putParcelable(LocationDialogFragment.OLD_LATLNG_ARG, oldLatLng);
                        locationDialogFragment.setArguments(bundle);
                        locationDialogFragment.setOnResultListener(new LocationDialogFragment.ResultListener() {
                            @Override
                            public void onNewValue(LatLng gps) {
                                String gpsToSave = gps.latitude + "," + gps.longitude;
                                item.saveValue(gpsToSave);
                            }
                        });
                        locationDialogFragment.show(getSupportFragmentManager(), DIALOG_HOME_LOCATION);
                }
                },
                new PrefItem.SubTitleGenerator() {
                    @Override
                    public String get(PrefItem item) {
                        String gps = (String) item.getValue();
                        if(gps != item.getDefaultValue()) {
                            String startLatitude = gps.split(",")[0];
                            String startLongitude = gps.split(",")[1];

                            return startLatitude + ", " + startLongitude;
                        }

                        return "Not set";
                    }
                }));

        mPrefItems.add(new PrefItem(this, R.drawable.ic_work_black_24dp, R.string.work_return_location, Prefs.RETURN_LOCATION, "", false,
                new PrefItem.OnClickListener() {
                    @Override
                    public void onClick(final PrefItem item) {
                        //home location needs to be set first
                        if(!PrefUtils.get(SettingsActivity.this, Prefs.START_LOCATION, "").equals("")) {
                            LocationDialogFragment locationDialogFragment = new LocationDialogFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString(LocationDialogFragment.TITLE_ARG, item.getTitle());
                            LatLng oldLatLng = PrefGpsToLatLng(item);
                            if(oldLatLng != null)
                                bundle.putParcelable(LocationDialogFragment.OLD_LATLNG_ARG, oldLatLng);
                            locationDialogFragment.setArguments(bundle);
                            locationDialogFragment.setOnResultListener(new LocationDialogFragment.ResultListener() {
                                @Override
                                public void onNewValue(LatLng gps) {
                                    if(gps.latitude != 0 || gps.longitude != 0) {
                                        String gpsToSave = gps.latitude + "," + gps.longitude;
                                        item.saveValue(gpsToSave);
                                    } else {
                                        item.saveValue("");
                                    }

                                }
                            });
                            locationDialogFragment.show(getSupportFragmentManager(), DIALOG_HOME_LOCATION);
                        } else {
                            Toast.makeText(SettingsActivity.this, "Home/start location needs to be set first", Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new PrefItem.SubTitleGenerator() {
                    @Override
                    public String get(PrefItem item) {
                        String gps = (String) item.getValue();
                        if(!gps.equals(item.getDefaultValue())) {
                            String startLatitude = gps.split(",")[0];
                            String startLongitude = gps.split(",")[1];

                            return startLatitude + ", " + startLongitude;
                        }
                        return "Same as start location";
                    }
                }));

        mPrefItems.add(new PrefItem(this, R.drawable.ic_access_time_black_24dp, R.string.notification_time, Prefs.NOTIFICATION_TIME, "", true,
                new PrefItem.OnClickListener() {
                    @Override
                    public void onClick(final PrefItem item) {
                        TimePickerDialogFragment timePickerDialogFragment = new TimePickerDialogFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(TimePickerDialogFragment.TITLE_ARG, item.getTitle());
                        String currentSetTime = (String) item.getValue();
                        if(!currentSetTime.equals(item.getDefaultValue()))
                            bundle.putString(TimePickerDialogFragment.CURRENT_SET_TIME_ARG, currentSetTime);
                        timePickerDialogFragment.setArguments(bundle);
                        timePickerDialogFragment.setOnResultListener(new TimePickerDialogFragment.ResultListener() {
                            @Override
                            public void onNewValue(String time) {
                                item.saveValue(time);
                                if(time != null && !time.equals("") && !time.equals("-1")) {
                                    int hour = Integer.valueOf(time.split(":")[0]);
                                    int minute = Integer.valueOf(time.split(":")[1]);
                                    BootReceiver.scheduleAlarms(SettingsActivity.this, BootReceiver.shouldTriggerNextDay(hour, minute));
                                } else {
                                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                    Intent serviceIntent = new Intent(SettingsActivity.this, BikeService.class);
                                    PendingIntent servicePendingIntent = PendingIntent.getService(SettingsActivity.this, 0, serviceIntent, 0);
                                    alarmManager.cancel(servicePendingIntent);
                                }
                            }
                        });

                        timePickerDialogFragment.show(getSupportFragmentManager(), DIALOG_TIME_PICKER);

                    }
                },
                new PrefItem.SubTitleGenerator() {
                    @Override
                    public String get(PrefItem item) {
                        String time = (String) item.getValue();
                        if(!time.equals(item.getDefaultValue())) {
                            if(!time.equals("-1"))
                                return time;
                            else
                                return "Notification disabled";
                        }

                        return "Not set";
                    }
                }));

        if(mRecyclerView.getAdapter() != null)
            mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    private LatLng PrefGpsToLatLng(PrefItem item) {
        String gps = (String) item.getValue();
        if(!gps.equals(item.getDefaultValue())) {
            String latitude = gps.split(",")[0];
            String longitude = gps.split(",")[1];

            return new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
        } else if(item.getTitle().equals(getString(R.string.work_return_location))){
            String current = PrefUtils.get(this, Prefs.START_LOCATION, "");
            String latitude = current.split(",")[0];
            String longitude = current.split(",")[1];

            return new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
        }
        return null;
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        refreshItems();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (isUseChangeablePref(key)) {
            refreshItems();
        }
    }

    private boolean isUseChangeablePref(String key) {
        boolean b = false;
        for (Object item : mPrefItems) {
            if (item instanceof PrefItem) {
                PrefItem pref = (PrefItem) item;
                if (pref.getPrefKey().equals(key))
                    b = true;
            }
        }
        return b;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PrefUtils.getPrefs(this).unregisterOnSharedPreferenceChangeListener(this);
    }

}
