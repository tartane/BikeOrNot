package com.alert.bikeornot.activities;

import android.net.Uri;
import android.os.Bundle;
import com.alert.bikeornot.R;
import com.alert.bikeornot.fragments.SetupNotificationTimeFragment;

public class SetupActivity extends BaseActivity implements SetupNotificationTimeFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_setup, false);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
