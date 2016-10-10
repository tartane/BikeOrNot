package com.alert.bikeornot;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.zetterstrom.com.forecast.ForecastClient;
import android.zetterstrom.com.forecast.ForecastConfiguration;
import android.zetterstrom.com.forecast.models.DataPoint;
import android.zetterstrom.com.forecast.models.Forecast;
import android.zetterstrom.com.forecast.models.Unit;

import com.alert.bikeornot.enums.EBikeOrNot;
import com.alert.bikeornot.models.BikeOrNotResponse;
import com.alert.bikeornot.preferences.Prefs;
import com.alert.bikeornot.utilities.FileUtils;
import com.alert.bikeornot.utilities.PrefUtils;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BikeManager {

    public static BikeOrNotResponse BikeOrNotDaily(DataPoint dataPoint) {

        EBikeOrNot currentStatus;

        if(dataPoint.getPrecipProbability() >= 0.50) {
            currentStatus = EBikeOrNot.No;
        } else if(dataPoint.getPrecipProbability() >= 0.30 && dataPoint.getPrecipProbability() < 0.50) {
            currentStatus = EBikeOrNot.Maybe;
        } else {
            currentStatus = EBikeOrNot.Yes;
        }

        return new BikeOrNotResponse(currentStatus);
    }

    public static BikeOrNotResponse BikeOrNotHourly(ArrayList<DataPoint> dataPoints) {


        int goingStartTimeHour = PrefUtils.get(App.getContext(), Prefs.START_TIME_HOUR, 8);
        int goingStartTimeMinute = PrefUtils.get(App.getContext(), Prefs.START_TIME_MINUTE, 45);

        int returnStartTimeHour = PrefUtils.get(App.getContext(), Prefs.START_TIME_HOUR, 17);
        int returnStartTimeMinute = PrefUtils.get(App.getContext(), Prefs.START_TIME_MINUTE, 00);

        int rideLengthMinute = PrefUtils.get(App.getContext(), Prefs.RIDE_LENGTH_MINUTE, 0);

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
        for(DataPoint dataPoint: dataPoints) {

            forecastCal.setTime(dataPoint.getTime());
            int forecastHour = forecastCal.get(Calendar.HOUR_OF_DAY);

            if(hoursToCheck.contains(forecastHour)) {
                dataPointToAnalyse.add(dataPoint);
            }

        }

        EBikeOrNot currentStatus = EBikeOrNot.Unknown;
        if(dataPointToAnalyse.size() > 0) {
            //TODO review the calculate. maybe an average of all hours precipitation? The values should come from the prefs.
            for (DataPoint datapoint : dataPointToAnalyse) {
                if (datapoint.getPrecipProbability() >= 0.50) {
                    currentStatus = EBikeOrNot.No;
                    break;
                } else if (datapoint.getPrecipProbability() >= 0.30 && datapoint.getPrecipProbability() < 0.50) {
                    currentStatus = EBikeOrNot.Maybe;
                    break;
                } else {
                    currentStatus = EBikeOrNot.Yes;
                }
            }
        } else {

            Calendar todayCal = Calendar.getInstance();
            //We don't have any point to analyse (time is probably after the start time of prefs)
            //Check the upcoming hours...
            for (DataPoint datapoint : dataPoints) {
                forecastCal.setTime(datapoint.getTime());

                if(forecastCal.get(Calendar.HOUR_OF_DAY) >= todayCal.get(Calendar.HOUR_OF_DAY)) {
                    if (datapoint.getPrecipProbability() >= 0.50) {
                        currentStatus = EBikeOrNot.No;
                        break;
                    } else if (datapoint.getPrecipProbability() >= 0.30 && datapoint.getPrecipProbability() < 0.50) {
                        currentStatus = EBikeOrNot.Maybe;
                        break;
                    } else {
                        currentStatus = EBikeOrNot.Yes;
                    }
                }
            }
        }

        return new BikeOrNotResponse(currentStatus);

    }

    public static ArrayList<DataPoint> GetTodayDatapoints(Forecast forecast) {
        ArrayList<DataPoint> todayDatapoints = new ArrayList<>();
        Calendar todayCal = Calendar.getInstance();
        Calendar forecastCal = Calendar.getInstance();
        for(DataPoint dataPoint: forecast.getHourly().getDataPoints()) {
            forecastCal.setTime(dataPoint.getTime());
            int day = todayCal.get(Calendar.DAY_OF_MONTH);

            if(day == forecastCal.get(Calendar.DAY_OF_MONTH)) {
                todayDatapoints.add(dataPoint);
            }
        }
        return todayDatapoints;
    }

    //Everything greater than today
    public static ArrayList<DataPoint> GetWeeklyDatapoints(Forecast forecast) {
        ArrayList<DataPoint> weeklyDatapoints = new ArrayList<>();
        Calendar todayCal = Calendar.getInstance();
        Calendar forecastCal = Calendar.getInstance();
        for(DataPoint dataPoint: forecast.getDaily().getDataPoints()) {
            forecastCal.setTime(dataPoint.getTime());

            if(forecastCal.get(Calendar.DAY_OF_MONTH) > todayCal.get(Calendar.DAY_OF_MONTH)) {
                weeklyDatapoints.add(dataPoint);
            }
        }
        return weeklyDatapoints;
    }

    public static void ShowNotification(BikeOrNotResponse bikeResponse) {

        int bicycleDrawable = R.drawable.ic_bicycle_white;

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(App.getContext())
                            .setColor(bikeResponse.getColor())
                            .setSmallIcon(bicycleDrawable)
                            .setContentTitle(bikeResponse.getTitle())
                            .setContentText(bikeResponse.getText());

        NotificationManager notificationManager =
                (NotificationManager) App.getContext().getSystemService(App.getContext().NOTIFICATION_SERVICE);

        notificationManager.notify(321, builder.build());
    }

    public static void FetchWeatherApi(final Context context, final Callback<Forecast> callback) {
        //TODO default unit should be in the settings
        ForecastConfiguration configuration =
                new ForecastConfiguration.Builder(context.getString(R.string.api_key)).setDefaultUnit(Unit.SI).build();

        ForecastClient.create(configuration);

        //If the user decide a different location for the going/return, i'll need to do 2 request
        String gps = PrefUtils.get(context, Prefs.START_LOCATION, "0,0");
        double startLatitude = Double.valueOf(gps.split(",")[0]);
        double startLongitude = Double.valueOf(gps.split(",")[1]);

        ForecastClient.getInstance()
                .getForecast(startLatitude, startLongitude, new Callback<Forecast>() {
                    @Override
                    public void onResponse(Call<Forecast> forecastCall, Response<Forecast> response) {
                        if (response.isSuccessful()) {
                            //Save for offline uses
                            FileUtils.writeObjectToFile(context, response.body());
                            callback.onResponse(forecastCall, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<Forecast> forecastCall, Throwable t) {
                        callback.onFailure(forecastCall, t);
                    }
                });
    }
}
