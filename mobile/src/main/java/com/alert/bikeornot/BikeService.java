package com.alert.bikeornot;

import android.app.IntentService;
import android.content.Intent;
import android.zetterstrom.com.forecast.models.DataPoint;
import android.zetterstrom.com.forecast.models.Forecast;

import com.alert.bikeornot.enums.EBikeOrNot;
import com.alert.bikeornot.models.BikeOrNotResponse;
import com.alert.bikeornot.utilities.FileUtils;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BikeService extends IntentService {

    public BikeService() {
        super("BikeService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Fetch api, display notification.
        BikeManager.FetchWeatherApi(this, new Callback<Forecast>() {
            @Override
            public void onResponse(Call<Forecast> call, Response<Forecast> response) {
                Forecast forecast = response.body();



                BikeOrNotResponse bikeResponse = BikeManager.BikeOrNotHourly(BikeManager.GetTodayDatapoints(forecast));
                BikeManager.ShowNotification(bikeResponse);

                //Schedule the next alarm.
                BootReceiver.scheduleAlarms(BikeService.this, true);
            }

            @Override
            public void onFailure(Call<Forecast> call, Throwable t) {
                BikeManager.ShowNotification(new BikeOrNotResponse(EBikeOrNot.Unknown));
            }
        });

    }
}
