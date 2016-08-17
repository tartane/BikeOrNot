package com.alert.bikeornot;

import android.app.IntentService;
import android.content.Intent;
import android.zetterstrom.com.forecast.ForecastClient;
import android.zetterstrom.com.forecast.ForecastConfiguration;
import android.zetterstrom.com.forecast.models.Forecast;

import com.alert.bikeornot.enums.EBikeOrNot;
import com.alert.bikeornot.preferences.Prefs;
import com.alert.bikeornot.utilities.FileUtils;
import com.alert.bikeornot.utilities.PrefUtils;

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
                            //Save for offline uses
                            FileUtils.writeObjectToFile(BikeService.this, response.body());

                            EBikeOrNot currentStatus = BikeManager.BikeOrNot(forecast);

                            BikeManager.ShowNotification(currentStatus);

                            //Schedule the next alarm.
                            BootReceiver.scheduleAlarms(BikeService.this, true);
                        }
                    }

                    @Override
                    public void onFailure(Call<Forecast> forecastCall, Throwable t) {
                        BikeManager.ShowNotification(EBikeOrNot.Unknown);
                    }
                });
    }

}
