package com.bikeornot.utilities;

import android.content.Context;

import com.bikeornot.Constants;
import com.bikeornot.preferences.ObscuredSharedPreferences;

public class PrefUtils {

    /**
     * Clear the central {@link ObscuredSharedPreferences}
     *
     * @param context Context
     */
    public static void clear(Context context) {
        getPrefs(context).edit().clear().apply();
    }

    /**
     * Save a string to the central {@link ObscuredSharedPreferences}
     *
     * @param context Context
     * @param key     Key of the preference
     * @param value   Value of the preference
     */
    public static void save(Context context, String key, String value) {
        getPrefs(context).edit().putString(key, value).apply();
    }

    /**
     * Get a saved string from the central {@link ObscuredSharedPreferences}
     *
     * @param context      Context
     * @param key          Key of the preference
     * @param defaultValue Default
     * @return The saved String
     */
    public static String get(Context context, String key, String defaultValue) {
        return getPrefs(context).getString(key, defaultValue);
    }

    /**
     * Save a boolean to the central {@link ObscuredSharedPreferences}
     *
     * @param context Context
     * @param key     Key of the preference
     * @param value   Value of the preference
     */
    public static void save(Context context, String key, boolean value) {
        getPrefs(context).edit().putBoolean(key, value).apply();
    }

    /**
     * Get a saved boolean from the central {@link ObscuredSharedPreferences}
     *
     * @param context      Context
     * @param key          Key of the preference
     * @param defaultValue Default
     * @return The saved bool
     */
    public static Boolean get(Context context, String key, boolean defaultValue) {
        return getPrefs(context).getBoolean(key, defaultValue);
    }

    /**
     * Save a long to the central {@link ObscuredSharedPreferences}
     *
     * @param context Context
     * @param key     Key of the preference
     * @param value   Value of the preference
     */
    public static void save(Context context, String key, long value) {
        getPrefs(context).edit().putLong(key, value).apply();
    }

    /**
     * Get a saved long from the central {@link ObscuredSharedPreferences}
     *
     * @param context      Context
     * @param key          Key of the preference
     * @param defaultValue Default
     * @return The saved long
     */
    public static long get(Context context, String key, long defaultValue) {
        return getPrefs(context).getLong(key, defaultValue);
    }

    /**
     * Save a int to the central {@link ObscuredSharedPreferences}
     *
     * @param context Context
     * @param key     Key of the preference
     * @param value   Value of the preference
     */
    public static void save(Context context, String key, int value) {
        getPrefs(context).edit().putInt(key, value).apply();
    }

    /**
     * Get a saved integer from the central {@link ObscuredSharedPreferences}
     *
     * @param context      Context
     * @param key          Key of the preference
     * @param defaultValue Default
     * @return The saved integer
     */
    public static int get(Context context, String key, int defaultValue) {
        return getPrefs(context).getInt(key, defaultValue);
    }

    /**
     * Save a double to the central {@link ObscuredSharedPreferences}
     *
     * @param context Context
     * @param key     Key of the preference
     * @param value   Value of the preference
     */
    public static void save(Context context, String key, double value) {
        getPrefs(context).edit().putLong(key, Double.doubleToRawLongBits(value));
    }

    /**
     * Get a saved double from the central {@link ObscuredSharedPreferences}
     *
     * @param context      Context
     * @param key          Key of the preference
     * @param defaultValue Default
     * @return The saved integer
     */
    public static double get(Context context, String key, double defaultValue) {
        return Double.longBitsToDouble(getPrefs(context).getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    /**
     * Check if the central {@link ObscuredSharedPreferences} contains a preference that uses that key
     *
     * @param context Context
     * @param key     Key
     * @return {@code true} if there exists a preference
     */
    public static Boolean contains(Context context, String key) {
        return getPrefs(context).contains(key);
    }

    /**
     * Remove item from the central {@link ObscuredSharedPreferences} if it exists
     *
     * @param context Context
     * @param key     Key
     */
    public static void remove(Context context, String key) {
        getPrefs(context).edit().remove(key).apply();
    }

    /**
     * Get the central {@link ObscuredSharedPreferences}
     *
     * @param context Context
     * @return {@link ObscuredSharedPreferences}
     */
    public static ObscuredSharedPreferences getPrefs(Context context) {
        return new ObscuredSharedPreferences(context, context.getSharedPreferences(Constants.PREFS_FILE, Context.MODE_PRIVATE));
    }

}
