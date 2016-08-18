package com.alert.bikeornot.models;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.alert.bikeornot.App;
import com.alert.bikeornot.R;
import com.alert.bikeornot.enums.EBikeOrNot;

public class BikeOrNotResponse {
    private EBikeOrNot status;
    private String title;
    private String text;
    private int color;
    private int darkColor;
    private Drawable bikeDrawable;

    public BikeOrNotResponse(EBikeOrNot status) {
        this.status = status;
        switch(this.status) {
            /*TODO It would be nice to add lots of random texts and titles.*/
            case Yes:
                title = "Bike today!";
                text = "Weather looks nice today. Get on your bike!";
                color = ContextCompat.getColor(App.getContext(), R.color.greenPrimary);
                darkColor = ContextCompat.getColor(App.getContext(), R.color.greenPrimaryDark);
                bikeDrawable = ContextCompat.getDrawable(App.getContext(), R.drawable.circle_bike_green);
                break;
            case Maybe:
                title = "Feeling lucky today?";
                text = "then take the chance and get on your bike!";
                color = ContextCompat.getColor(App.getContext(), R.color.orangePrimary);
                darkColor = ContextCompat.getColor(App.getContext(), R.color.orangePrimaryDark);
                bikeDrawable = ContextCompat.getDrawable(App.getContext(), R.drawable.circle_bike_orange);
                break;
            case No:
                title = "Eww.";
                text = "Biking might not be the best idea today.";
                color = ContextCompat.getColor(App.getContext(), R.color.redPrimary);
                darkColor = ContextCompat.getColor(App.getContext(), R.color.redPrimaryDark);
                bikeDrawable = ContextCompat.getDrawable(App.getContext(), R.drawable.circle_bike_red);
                break;
            case Unknown:
            default:
                title = "Unknown weather.";
                text = "Unable to get the forecast of today... Your call.";
                color = ContextCompat.getColor(App.getContext(), R.color.blackPrimary);
                darkColor = ContextCompat.getColor(App.getContext(), R.color.blackPrimaryDark);
                bikeDrawable = ContextCompat.getDrawable(App.getContext(), R.drawable.circle_bike_black);
                break;
        }
    }

    public int getDarkColor() {
        return darkColor;
    }

    public void setDarkColor(int darkColor) {
        this.darkColor = darkColor;
    }

    public Drawable getBikeDrawable() {
        return bikeDrawable;
    }

    public void setBikeDrawable(Drawable bikeDrawable) {
        this.bikeDrawable = bikeDrawable;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public EBikeOrNot getStatus() {
        return status;
    }

    public void setStatus(EBikeOrNot status) {
        this.status = status;
    }
}
