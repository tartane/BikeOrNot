package com.bikeornot.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.bikeornot.BootReceiver;
import com.bikeornot.R;
import com.bikeornot.preferences.Prefs;
import com.bikeornot.utilities.PrefUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int notificationHour = PrefUtils.get(this, Prefs.NOTIFICATION_HOUR, 8);
        int notificationMinute = PrefUtils.get(this, Prefs.NOTIFICATION_MINUTE, 0);

        //TODO this should be called after the setup. the next day should be decide when the user setup the app
        BootReceiver.scheduleAlarms(this,  BootReceiver.shouldTriggerNextDay(notificationHour, notificationMinute));
    }
}
