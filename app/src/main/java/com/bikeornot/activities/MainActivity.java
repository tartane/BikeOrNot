package com.bikeornot.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bikeornot.BootReceiver;
import com.bikeornot.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //TODO this should be called after the setup.
        BootReceiver.scheduleAlarms(this);
    }
}
