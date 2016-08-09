package com.alert.bikeornot.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.alert.bikeornot.BootReceiver;
import com.alert.bikeornot.R;
import com.alert.bikeornot.preferences.Prefs;
import com.alert.bikeornot.utilities.PrefUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.txtHour)
    EditText txtHour;

    @Bind(R.id.txtMinute)
    EditText txtMinute;

    @Bind(R.id.btnSchedule)
    Button btnSchedule;

    @Bind(R.id.layBikeStatus)
    RelativeLayout layBikeStatus;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isConfigured = PrefUtils.get(this, Prefs.IS_CONFIGURED, false);
        if(!isConfigured) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            finish();
        }
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        btnSchedule.setOnClickListener(this);

        layBikeStatus.setPadding(0, getStatusBarHeight(), 0, 0);
    }


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
            //dev only
            case R.id.btnSchedule:
                int notificationHour = Integer.valueOf(txtHour.getText().toString());
                int notificationMinute = Integer.valueOf(txtMinute.getText().toString());

                PrefUtils.save(this, Prefs.NOTIFICATION_HOUR, notificationHour);
                PrefUtils.save(this, Prefs.NOTIFICATION_MINUTE, notificationMinute);

                //TODO this should be called after the setup. the next day should be decide when the user setup the app
                BootReceiver.scheduleAlarms(this, BootReceiver.shouldTriggerNextDay(notificationHour, notificationMinute));
                break;
        }
    }
}
