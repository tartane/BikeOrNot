package com.alert.bikeornot.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        btnSchedule.setOnClickListener(this);


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
