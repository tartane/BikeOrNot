
package com.bikeornot.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Forecast {

    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("timezone")
    @Expose
    private String timezone;
    @SerializedName("offset")
    @Expose
    private Integer offset;
    @SerializedName("currently")
    @Expose
    private DataPoint currently;
    @SerializedName("minutely")
    @Expose
    private DataBlock minutely;
    @SerializedName("hourly")
    @Expose
    private DataBlock hourly;
    @SerializedName("daily")
    @Expose
    private DataBlock daily;
    @SerializedName("flags")
    @Expose
    private Flags flags;

    /**
     * 
     * @return
     *     The latitude
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * 
     * @param latitude
     *     The latitude
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * 
     * @return
     *     The longitude
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * 
     * @param longitude
     *     The longitude
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * 
     * @return
     *     The timezone
     */
    public String getTimezone() {
        return timezone;
    }

    /**
     * 
     * @param timezone
     *     The timezone
     */
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    /**
     * 
     * @return
     *     The offset
     */
    public Integer getOffset() {
        return offset;
    }

    /**
     * 
     * @param offset
     *     The offset
     */
    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    /**
     * 
     * @return
     *     The currently
     */
    public DataPoint getCurrently() {
        return currently;
    }

    /**
     * 
     * @param currently
     *     The currently
     */
    public void setCurrently(DataPoint currently) {
        this.currently = currently;
    }

    /**
     * 
     * @return
     *     The minutely
     */
    public DataBlock getMinutely() {
        return minutely;
    }

    /**
     * 
     * @param minutely
     *     The minutely
     */
    public void setMinutely(DataBlock minutely) {
        this.minutely = minutely;
    }

    /**
     * 
     * @return
     *     The hourly
     */
    public DataBlock getHourly() {
        return hourly;
    }

    /**
     * 
     * @param hourly
     *     The hourly
     */
    public void setHourly(DataBlock hourly) {
        this.hourly = hourly;
    }

    /**
     * 
     * @return
     *     The daily
     */
    public DataBlock getDaily() {
        return daily;
    }

    /**
     * 
     * @param daily
     *     The daily
     */
    public void setDaily(DataBlock daily) {
        this.daily = daily;
    }

    /**
     * 
     * @return
     *     The flags
     */
    public Flags getFlags() {
        return flags;
    }

    /**
     * 
     * @param flags
     *     The flags
     */
    public void setFlags(Flags flags) {
        this.flags = flags;
    }

}
