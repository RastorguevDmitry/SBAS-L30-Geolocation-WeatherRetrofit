package com.rdi.geolocation.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AllWeatherData {
    @SerializedName("main")
    @Expose
    WetherDataMain mWetherDataMain;

    public WetherDataMain getWetherDataMain() {
        return mWetherDataMain;
    }

    public void setWetherDataMain(WetherDataMain wetherDataMain) {
        mWetherDataMain = wetherDataMain;
    }

    public AllWeatherData(WetherDataMain wetherDataMain) {
        mWetherDataMain = wetherDataMain;
    }


}
