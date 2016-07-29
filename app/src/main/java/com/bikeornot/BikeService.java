package com.bikeornot;

import android.app.IntentService;
import android.content.Intent;

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


    }

    public void FetchApi() {
        OkHttpClient client = App.getHttpClient();

        final Request request = new Request.Builder()
                .url(forecastUrl + getString(R.string.api_key) + "/")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                if(response.isSuccessful()) {
                    Gson gson = App.getGson();
                    String json = response.body().string();
                    Forecast forecast =  gson.fromJson(json, Forecast.class);
                }
            }
        });
    }

    public void BikeOrNot() {
        //TODO evaluate the forecast data.
        ShowNotification(BikeOrNot.Yes);
    }

    public void ShowNotification(BikeOrNot bikeOrNot) {

    }
}
