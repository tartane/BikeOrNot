package com.alert.bikeornot;

import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.zetterstrom.com.forecast.models.DataPoint;
import android.zetterstrom.com.forecast.models.Forecast;

import com.alert.bikeornot.enums.EBikeOrNot;
import com.alert.bikeornot.preferences.Prefs;
import com.alert.bikeornot.utilities.PrefUtils;

import java.util.ArrayList;
import java.util.Calendar;

public class BikeManager {
    public static EBikeOrNot BikeOrNot(Forecast forecast) {

        int goingStartTimeHour = PrefUtils.get(App.getContext(), Prefs.GOING_START_TIME_HOUR, 8);
        int goingStartTimeMinute = PrefUtils.get(App.getContext(), Prefs.GOING_START_TIME_MINUTE, 45);

        int returnStartTimeHour = PrefUtils.get(App.getContext(), Prefs.RETURN_START_TIME_HOUR, 17);
        int returnStartTimeMinute = PrefUtils.get(App.getContext(), Prefs.RETURN_START_TIME_MINUTE, 00);

        int rideLengthMinute = PrefUtils.get(App.getContext(), Prefs.RIDE_LENGTH_MINUTE, 25);

        ArrayList<Integer> hoursToCheck = new ArrayList();
        hoursToCheck.add(goingStartTimeHour);
        int startMinuteIncludingRide = goingStartTimeMinute + rideLengthMinute;
        if(startMinuteIncludingRide >= 60) {
            int hours = startMinuteIncludingRide / 60;
            for(int i = 1; i <= hours; i++) {
                int hourToAdd = goingStartTimeHour + i;
                //24-hour clock
                if(hourToAdd > 23) {
                    hourToAdd -= 24;
                }
                hoursToCheck.add(hourToAdd);
            }
        }

        hoursToCheck.add(returnStartTimeHour);
        int endMinuteIncludingRide = returnStartTimeMinute + rideLengthMinute;
        if(endMinuteIncludingRide >= 60) {
            int hours = endMinuteIncludingRide / 60;
            for(int i = 1; i <= hours; i++) {
                int hourToAdd = returnStartTimeHour + i;
                //24-hour clock
                if(hourToAdd > 23) {
                    hourToAdd -= 24;
                }
                hoursToCheck.add(hourToAdd);
            }
        }

        //Get the datapoints
        ArrayList<DataPoint> dataPointToAnalyse = new ArrayList();

        Calendar forecastCal = Calendar.getInstance();
        Calendar localCal = Calendar.getInstance();
        for(DataPoint dataPoint: forecast.getHourly().getDataPoints()) {
            forecastCal.setTime(dataPoint.getTime());

            int forecastDay = forecastCal.get(Calendar.DAY_OF_MONTH);
            int forecastHour = forecastCal.get(Calendar.HOUR_OF_DAY);

            int localDay = localCal.get(Calendar.DAY_OF_MONTH);

            if(forecastDay == localDay) {
                if(hoursToCheck.contains(forecastHour)) {
                    dataPointToAnalyse.add(dataPoint);
                }
            }
        }

        EBikeOrNot currentStatus = EBikeOrNot.Unknown;

        //TODO review the calculate. maybe an average of all hours precipitation? The values should come from the prefs.
        for(DataPoint datapoint: dataPointToAnalyse) {
            if(datapoint.getPrecipProbability() >= 0.50) {
                currentStatus = EBikeOrNot.No;
                break;
            } else if(datapoint.getPrecipProbability() >= 0.30 && datapoint.getPrecipProbability() < 0.50) {
                currentStatus = EBikeOrNot.Maybe;
                break;
            } else {
                currentStatus = EBikeOrNot.Yes;
            }
        }

        return currentStatus;

    }

    public static void ShowNotification(EBikeOrNot bikeOrNot) {

        int bicycleDrawable = R.drawable.ic_bicycle_white;
        int backgroundIconColor;
        String title;
        String text;

        switch(bikeOrNot) {
            /*TODO It would be nice to add lots of random texts and titles.*/
            case Yes:
                title = "Bike today!";
                text = "Weather looks nice today. Get on your bike!";
                backgroundIconColor = ContextCompat.getColor(App.getContext(), R.color.greenPrimary);
                break;
            case Maybe:
                title = "Feeling lucky today?";
                text = "then take the chance and get on your bike!";
                backgroundIconColor = ContextCompat.getColor(App.getContext(), R.color.orangePrimary);
                break;
            case No:
                title = "Eww.";
                text = "Biking might not be the best idea today.";
                backgroundIconColor = ContextCompat.getColor(App.getContext(), R.color.redPrimary);
                break;
            case Unknown:
            default:
                bicycleDrawable = R.drawable.ic_bicycle_white;
                title = "Unknown weather.";
                text = "Unable to get the forecast of today... Your call.";
                backgroundIconColor = ContextCompat.getColor(App.getContext(), android.R.color.black);
                break;
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(App.getContext())
                            .setColor(backgroundIconColor)
                            .setSmallIcon(bicycleDrawable)
                            .setContentTitle(title)
                            .setContentText(text);

        NotificationManager notificationManager =
                (NotificationManager) App.getContext().getSystemService(App.getContext().NOTIFICATION_SERVICE);

        notificationManager.notify(321, builder.build());
    }
}
