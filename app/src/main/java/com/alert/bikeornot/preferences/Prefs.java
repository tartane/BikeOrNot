package com.alert.bikeornot.preferences;

public class Prefs {
    //Whether the user has configured the app or not
    public static final String IS_CONFIGURED = "is_setup";

    //The time the notification will be displayed. Must be before Going Time.
    public static final String NOTIFICATION_HOUR = "notification_hour";
    public static final String NOTIFICATION_MINUTE = "notification_minute";

    //When the user is leaving home.
    public static final String GOING_START_TIME_HOUR = "going_start_time_hour";
    public static final String GOING_START_TIME_MINUTE = "going_start_time_minute";

    //User home location
    public static final String GOING_LATITUDE = "going_latitude";
    public static final String GOING_LONGITUDE = "going_longitude";

    //When user is leaving work
    public static final String RETURN_START_TIME_HOUR = "return_start_time_hour";
    public static final String RETURN_START_TIME_MINUTE = "return_start_time_minute";

    //User work location
    public static final String RETURN_LATITUDE = "return_latitude";
    public static final String RETURN_LONGITUDE = "return_longitude";

    //Time from home to work. Single length for now (same for going and return)
    public static final String RIDE_LENGTH_MINUTE = "ride_length_minute";

}