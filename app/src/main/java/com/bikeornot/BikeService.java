package com.bikeornot;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.zetterstrom.com.forecast.ForecastClient;
import android.zetterstrom.com.forecast.ForecastConfiguration;
import android.zetterstrom.com.forecast.models.DataPoint;
import android.zetterstrom.com.forecast.models.Forecast;

import com.bikeornot.preferences.Prefs;
import com.bikeornot.utilities.PrefUtils;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BikeService extends IntentService {

    enum EBikeOrNot {
        Yes,
        No,
        Maybe,
        Unknown
    }

    public BikeService() {
        super("BikeService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Fetch api, display notification.
        FetchWeatherApi();

    }

    public void FetchWeatherApi() {
        ForecastConfiguration configuration =
                new ForecastConfiguration.Builder(getString(R.string.api_key)).build();

        ForecastClient.create(configuration);

        //If the user decide a different location for the going/return, i'll need to do 2 request
        double goingLatitude = PrefUtils.get(this, Prefs.GOING_LATITUDE, 45.4765450);
        double goingLongitude = PrefUtils.get(this, Prefs.GOING_LONGITUDE, -75.7012720);

        ForecastClient.getInstance()
                .getForecast(goingLatitude, goingLongitude, new Callback<Forecast>() {
                    @Override
                    public void onResponse(Call<Forecast> forecastCall, Response<Forecast> response) {
                        if (response.isSuccessful()) {
                            Forecast forecast = response.body();
                            BikeOrNot(forecast);
                        }
                    }

                    @Override
                    public void onFailure(Call<Forecast> forecastCall, Throwable t) {
                        ShowNotification(EBikeOrNot.Unknown);
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

        ShowNotification(currentStatus);

        //Schedule the next alarm.
        BootReceiver.scheduleAlarms(this, true);
    }



    public void ShowNotification(EBikeOrNot bikeOrNot) {

        int bicycleDrawable = -1;
        String title;
        String text;

        switch(bikeOrNot) {
            /*TODO It would be nice to add lots of random texts and titles.*/
            case Yes:
                bicycleDrawable = R.drawable.ic_bicycle_green;
                title = "Bike today!";
                text = "Weather looks nice today. Get on your bike!";
                break;
            case Maybe:
                bicycleDrawable = R.drawable.ic_bicycle_yellow;
                title = "Feeling lucky today?";
                text = "then take the chance and get on your bike!";
                break;
            case No:
                bicycleDrawable = R.drawable.ic_bicycle_red;
                title = "Eww.";
                text = "Biking might not be the best idea today.";
                break;
            case Unknown:
            default:
                bicycleDrawable = R.drawable.ic_bicycle_white;
                title = "Unknown weather.";
                text = "Unable to get the forecast of today... Your call.";
                break;
        }



        int color = ContextCompat.getColor(this, R.color.colorPrimary);
        //Bitmap bm = BitmapFactory.decodeResource(getResources(), bicycleDrawable);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setColor(color)
                        .setSmallIcon(bicycleDrawable)
                        .setContentTitle(title)
                        .setContentText(text);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(321, builder.build());
    }
}
