package com.bikeornot;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bikeornot.preferences.Prefs;
import com.bikeornot.utilities.PrefUtils;

import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Make sure the user has setup the app before showing notification
        if(PrefUtils.get(context, Prefs.IS_SETUP, false)){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());

            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            int currentMinute = calendar.get(Calendar.MINUTE);

            int goingStartTimeHour = PrefUtils.get(context, Prefs.GOING_START_TIME_HOUR, 15);
            int goingStartTimeMinute = PrefUtils.get(context, Prefs.GOING_START_TIME_MINUTE, 36);

            if(currentHour > goingStartTimeHour || (currentHour == goingStartTimeHour && currentMinute > goingStartTimeMinute)) {
                scheduleAlarms(context, true);
            } else {
                scheduleAlarms(context, false);
            }
        }
    }

    public static void scheduleAlarms(Context context, boolean nextDay) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent serviceIntent = new Intent(context, BikeService.class);
        PendingIntent servicePendingIntent = PendingIntent.getService(context, 0, serviceIntent, 0);

        int goingStartTimeHour = PrefUtils.get(context, Prefs.GOING_START_TIME_HOUR, 17);
        int goingStartTimeMinute = PrefUtils.get(context, Prefs.GOING_START_TIME_MINUTE, 56);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, goingStartTimeHour);
        calendar.set(Calendar.MINUTE, goingStartTimeMinute);

        //Repeating alarm
        if(nextDay) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        if (android.os.Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), servicePendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), servicePendingIntent);
        }

    }
}
