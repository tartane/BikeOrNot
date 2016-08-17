package com.alert.bikeornot;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import java.util.concurrent.TimeUnit;

public class App extends Application {

    private static OkHttpClient sHttpClient;
    private static Gson mGson;
    private static Context mContext;
    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

    }

    public static OkHttpClient getHttpClient() {
        if (sHttpClient == null) {
            sHttpClient = new OkHttpClient();
            sHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
            sHttpClient.setReadTimeout(10, TimeUnit.SECONDS);
        }
        return sHttpClient;
    }


    public static Gson getGson() {
        if (mGson == null) {
            mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
        }
        return mGson;
    }
}
