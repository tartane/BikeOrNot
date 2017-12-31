package com.alert.bikeornot.preferences;

public class Prefs {
    //Whether the user has configured the app or not
    public static final String IS_CONFIGURED = "is_setup";

    //The time the notification will be displayed. Must be before Going Time.
    //HH:MM
    public static final String NOTIFICATION_TIME = "notification_time";

    //When the user is leaving home.
    public static final String START_TIME_HOUR = "start_time_hour";
    public static final String START_TIME_MINUTE = "start_time_minute";

    //User home location
    public static final String START_LOCATION = "start_location";

    public static final String LOCATION_LATITUDE = "location_latitude";
    public static final String LOCATION_LONGITUDE = "location_longitude";

    //When user is leaving work
    public static final String RETURN_TIME_HOUR = "return_time_hour";
    public static final String RETURN_TIME_MINUTE = "return_time_minute";

    //User work location
    public static final String RETURN_LOCATION = "return_location";

    //Time from home to work. Single length for now (same for going and return)
    public static final String RIDE_LENGTH_MINUTE = "ride_length_minute";

    //Time of the latest refresh in millis
    public static final String UPDATED_TIME = "updated_time";

}