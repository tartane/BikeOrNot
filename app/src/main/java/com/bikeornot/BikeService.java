package com.bikeornot;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.bikeornot.models.Forecast;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

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
