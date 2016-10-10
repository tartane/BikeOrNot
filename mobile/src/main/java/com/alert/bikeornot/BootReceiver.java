package com.alert.bikeornot;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.alert.bikeornot.preferences.Prefs;
import com.alert.bikeornot.utilities.PrefUtils;

import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Make sure the user has setup the app before showing notification
        if(PrefUtils.get(context, Prefs.IS_CONFIGURED, false)){
            String time = PrefUtils.get(context, Prefs.NOTIFICATION_TIME, "");
            if(!time.equals("")) {
                int notificationHour = Integer.valueOf(time.split(":")[0]);
                int notificationMinute = Integer.valueOf(time.split(":")[1]);

                if (shouldTriggerNextDay(notificationHour, notificationMinute)) {
                    scheduleAlarms(context, true);
                } else {
                    scheduleAlarms(context, false);
                }
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
        String time = PrefUtils.get(context, Prefs.NOTIFICATION_TIME, "");
        if(!time.equals("")) {

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent serviceIntent = new Intent(context, BikeService.class);
            PendingIntent servicePendingIntent = PendingIntent.getService(context, 0, serviceIntent, 0);

            int notificationHour = Integer.valueOf(time.split(":")[0]);
            int notificationMinute = Integer.valueOf(time.split(":")[1]);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, notificationHour);
            calendar.set(Calendar.MINUTE, notificationMinute);
            calendar.set(Calendar.SECOND, 0);

            //Repeating alarm or boot
            if(nextDay) {
                calendar.add(Calendar.DATE, 1);
            }

            long alarmTime = calendar.getTimeInMillis();

            final int SDK_INT = Build.VERSION.SDK_INT;
            if (SDK_INT < Build.VERSION_CODES.KITKAT) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, servicePendingIntent);
            }
            else if (Build.VERSION_CODES.KITKAT <= SDK_INT  && SDK_INT < Build.VERSION_CODES.M) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, servicePendingIntent);
            }
            else if (SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, servicePendingIntent);
            }
        }

    }
}
