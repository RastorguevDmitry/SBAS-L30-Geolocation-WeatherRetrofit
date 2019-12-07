package com.rdi.geolocation.data.model;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IWetherService {

    @GET("weather")
    Call<AllWeatherData> getCurrentWeather(
            @Query("lat") double latitude,
            @Query("lon") double longitude,

            @Query("appid") String apiKey,
            @Query("units") String units
    );
}
