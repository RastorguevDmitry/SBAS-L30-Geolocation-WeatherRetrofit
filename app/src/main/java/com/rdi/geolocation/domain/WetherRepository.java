package com.rdi.geolocation.domain;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rdi.geolocation.data.Constants;
import com.rdi.geolocation.data.model.IWetherService;
import com.rdi.geolocation.data.model.AllWeatherData;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class WetherRepository implements IWetherRepository {

    private final IWetherService mWetherApi;


    public WetherRepository() {
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        mWetherApi = retrofit.create(IWetherService.class);
    }

    @NonNull
    @Override
    public AllWeatherData loadWeather(Double latitude, Double longitude) throws IOException {
        Call<AllWeatherData> listCall = mWetherApi.getCurrentWeather(latitude, longitude, Constants.API_KEY, Constants.DEFAULT_UNITS);
        Response<AllWeatherData> response = listCall.execute();
        if (response.body() == null || response.errorBody() != null) {
            throw new IOException("Не удалось загрузить погоду");
        }
        AllWeatherData allWeatherData = response.body();
        return allWeatherData;
    }
}
