package com.rdi.geolocation.data.model;


import com.google.gson.annotations.*;
import com.google.gson.annotations.SerializedName;



public class WetherDataMain {

    @SerializedName("temp")
    @Expose
    private double mTemp;
    @SerializedName("pressure")
    @Expose
    private double mPressure;
    @SerializedName("humidity")
    @Expose
    private double mHumidity;
    @SerializedName("temp_min")
    @Expose
    private double mTempMin;
    @SerializedName("temp_max")
    @Expose
    private double mTempMax;

    public double getTemp() {
        return mTemp;
    }

    public void setTemp(double temp) {
        mTemp = temp;
    }

    public double getPressure() {
        return mPressure;
    }

    public void setPressure(double pressure) {
        mPressure = pressure;
    }

    public double getHumidity() {
        return mHumidity;
    }

    public void setHumidity(double humidity) {
        mHumidity = humidity;
    }

    public double getTempMin() {
        return mTempMin;
    }

    public void setTempMin(double tempMin) {
        mTempMin = tempMin;
    }

    public double getTempMax() {
        return mTempMax;
    }

    public void setTempMax(double tempMax) {
        mTempMax = tempMax;
    }

    public WetherDataMain(double temp, double pressure, double humidity, double tempMin, double tempMax) {
        mTemp = temp;
        mPressure = pressure;
        mHumidity = humidity;
        mTempMin = tempMin;
        mTempMax = tempMax;
    }
}
