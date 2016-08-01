package com.bikeornot;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.bikeornot.models.DataPoint;
import com.bikeornot.models.Forecast;
import com.bikeornot.preferences.Prefs;
import com.bikeornot.utilities.PrefUtils;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class BikeService extends IntentService {

    enum BikeOrNot {
        Yes,
        No,
        Maybe
    }

    private final String forecastUrl = "https://api.forecast.io/forecast/";

    public BikeService() {
        super("BikeService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Fetch api, display notification.
        FetchWeatherApi();

    }

    public void FetchWeatherApi() {
        OkHttpClient client = App.getHttpClient();

        final Request request = new Request.Builder()
                .url(forecastUrl + getString(R.string.api_key) + "/45.4765450,-75.7012720")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                if(response.isSuccessful()) {
                    try {
                        Gson gson = App.getGson();
                        String json = response.body().string();
                        Forecast forecast = gson.fromJson(json, Forecast.class);
                        BikeOrNot(forecast);
                    } catch(Exception e) {
                        String exception = e.getMessage();
                    }
                }
            }
        });
    }

    public void BikeOrNot(Forecast forecast) {

        int goingStartTimeHour = PrefUtils.get(this, Prefs.GOING_START_TIME_HOUR, 8);
        int goingStartTimeMinute = PrefUtils.get(this, Prefs.GOING_START_TIME_MINUTE, 45);

        int returnStartTimeHour = PrefUtils.get(this, Prefs.RETURN_START_TIME_HOUR, 17);
        int returnStartTimeMinute = PrefUtils.get(this, Prefs.RETURN_START_TIME_MINUTE, 00);

        int rideLengthMinute = PrefUtils.get(this, Prefs.RIDE_LENGTH_MINUTE, 25);

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
        for(DataPoint dataPoint: forecast.getHourly().getData()) {
            forecastCal.setTimeInMillis(dataPoint.getTime());

            int forecastDay = forecastCal.get(Calendar.DAY_OF_MONTH);
            int forecastHour = forecastCal.get(Calendar.HOUR_OF_DAY);

            int localDay = localCal.get(Calendar.DAY_OF_MONTH);

            if(forecastDay == localDay) {
                if(hoursToCheck.contains(forecastHour)) {
                    dataPointToAnalyse.add(dataPoint);
                }
            }
        }

        //TODO evaluate the forecast data.
        ShowNotification(BikeOrNot.Yes);
    }

    public void ShowNotification(BikeOrNot bikeOrNot) {

        int bicycleDrawable = -1;

        switch(bikeOrNot) {
            case Yes:
                bicycleDrawable = R.drawable.ic_bicycle_green;
                break;
            case Maybe:
                bicycleDrawable = R.drawable.ic_bicycle_yellow;
                break;
            case No:
                bicycleDrawable = R.drawable.ic_bicycle_red;
                break;
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(bicycleDrawable)
                        .setContentTitle("Bike today!")
                        .setContentText("Weather looks nice today. Get on your bike!");

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(321, builder.build());
    }
}
