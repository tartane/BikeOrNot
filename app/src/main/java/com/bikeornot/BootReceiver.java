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
            int notificationHour = PrefUtils.get(context, Prefs.NOTIFICATION_HOUR, 8);
            int notificationMinute = PrefUtils.get(context, Prefs.NOTIFICATION_MINUTE, 00);

            if(shouldTriggerNextDay(notificationHour, notificationMinute)) {
                scheduleAlarms(context, true);
            } else {
                scheduleAlarms(context, false);
            }
        }
    }

    //static for dev purpose only
    public static boolean shouldTriggerNextDay(int notificationHour, int notificationMinute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        calendar.set(Calendar.HOUR_OF_DAY, notificationHour);
        calendar.set(Calendar.MINUTE, notificationMinute);

        if(Calendar.getInstance().after(calendar)) {
            return true;
        } else {
            return false;
        }

    }

    public static void scheduleAlarms(Context context, boolean nextDay) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent serviceIntent = new Intent(context, BikeService.class);
        PendingIntent servicePendingIntent = PendingIntent.getService(context, 0, serviceIntent, 0);

        int notificationHour = PrefUtils.get(context, Prefs.NOTIFICATION_HOUR, 8);
        int notificationMinute = PrefUtils.get(context, Prefs.NOTIFICATION_MINUTE, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, notificationHour);
        calendar.set(Calendar.MINUTE, notificationMinute);

        //Repeating alarm or boot
        if(nextDay) {
            calendar.add(Calendar.DATE, 1);
        }

        long alarmTime = calendar.getTimeInMillis();
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, servicePendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, servicePendingIntent);
        }

    }
}
