package com.alert.bikeornot.activities;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.alert.bikeornot.R;
import com.alert.bikeornot.dialogs.LocationDialogFragment;
import com.alert.bikeornot.preferences.PrefItem;
import com.alert.bikeornot.preferences.Prefs;
import com.alert.bikeornot.utilities.PrefUtils;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private ArrayList<Object> mPrefItems = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;
    private final String DIALOG_HOME_LOCATION = "dialog_home_location";
    private final String DIALOG_WORK_LOCATION = "dialog_work_location";

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

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

        recyclerView.setLayoutManager(mLayoutManager);

        PrefUtils.getPrefs(this).registerOnSharedPreferenceChangeListener(this);

    }

    private void refreshItems() {
        mPrefItems = new ArrayList<>();
        mPrefItems.add(getString(R.string.location));
        mPrefItems.add(new PrefItem(this, R.drawable.ic_home_black_24dp, R.string.home_start_location, Prefs.START_LOCATION, "Not set",
                new PrefItem.OnClickListener() {
                    @Override
                    public void onClick(final PrefItem item) {
                        LocationDialogFragment locationDialogFragment = new LocationDialogFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(LocationDialogFragment.TITLE, item.getTitle());
                        locationDialogFragment.setArguments(bundle);
                        locationDialogFragment.setOnResultListener(new LocationDialogFragment.ResultListener() {
                            @Override
                            public void onNewValue(LatLng gps) {
                                String gpsToSave = gps.latitude + "," + gps.longitude;
                                item.saveValue(gpsToSave);
                            }
                        });
                        locationDialogFragment.show(getFragmentManager(), DIALOG_HOME_LOCATION);
                }
                },
                new PrefItem.SubTitleGenerator() {
                    @Override
                    public String get(PrefItem item) {
                        String gps = (String) item.getValue();
                        String startLatitude = gps.split(",")[0];
                        String startLongitude = gps.split(",")[1];

                        return startLatitude + ", " + startLongitude;
                    }
                }));

        mPrefItems.add(new PrefItem(this, R.drawable.ic_work_black_24dp, R.string.work_return_location, Prefs.RETURN_LOCATION, "Same as start location",
                new PrefItem.OnClickListener() {
                    @Override
                    public void onClick(final PrefItem item) {
                        LocationDialogFragment locationDialogFragment = new LocationDialogFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(LocationDialogFragment.TITLE, item.getTitle());
                        locationDialogFragment.setArguments(bundle);
                        locationDialogFragment.setOnResultListener(new LocationDialogFragment.ResultListener() {
                            @Override
                            public void onNewValue(LatLng gps) {

                            }
                        });
                        locationDialogFragment.show(getFragmentManager(), DIALOG_HOME_LOCATION);
                    }
                },
                new PrefItem.SubTitleGenerator() {
                    @Override
                    public String get(PrefItem item) {
                        String gps = (String) item.getValue();
                        String startLatitude = gps.split(",")[0];
                        String startLongitude = gps.split(",")[1];

                        return startLatitude + ", " + startLongitude;
                    }
                }));
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
