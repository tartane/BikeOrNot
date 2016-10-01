package com.alert.bikeornot.activities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.alert.bikeornot.R;
import com.alert.bikeornot.adapters.SettingsListAdapter;
import com.alert.bikeornot.dialogs.LocationDialogFragment;
import com.alert.bikeornot.dialogs.TimePickerDialogFragment;
import com.alert.bikeornot.preferences.PrefItem;
import com.alert.bikeornot.preferences.Prefs;
import com.alert.bikeornot.utilities.PrefUtils;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private ArrayList<Object> mPrefItems = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;
    private final String DIALOG_HOME_LOCATION = "dialog_home_location";
    private final String DIALOG_WORK_LOCATION = "dialog_work_location";
    private final String DIALOG_TIME_PICKER = "dialog_time_picker";
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.settings);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(mLayoutManager);

        PrefUtils.getPrefs(this).registerOnSharedPreferenceChangeListener(this);

        refreshItems();

        mRecyclerView.setAdapter(new SettingsListAdapter(mPrefItems));

    }

    private void refreshItems() {
        mPrefItems = new ArrayList<>();
        mPrefItems.add(getString(R.string.location));
        mPrefItems.add(new PrefItem(this, R.drawable.ic_home_black_24dp, R.string.home_start_location, Prefs.START_LOCATION, "",
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

        mPrefItems.add(new PrefItem(this, R.drawable.ic_work_black_24dp, R.string.work_return_location, Prefs.RETURN_LOCATION, "",
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

        mPrefItems.add(new PrefItem(this, R.drawable.ic_access_time_black_24dp, R.string.notification_time, Prefs.NOTIFICATION_TIME, "",
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
                            return time;
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
