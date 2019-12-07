package com.rdi.geolocation.domain;

import androidx.annotation.NonNull;

import com.rdi.geolocation.data.model.AllWeatherData;


import java.io.IOException;


/**
 * Репозиторий для загрузки списка валют
 **/
public interface IWetherRepository {

    /**
     * Загружает список валют
     * @return
     */
    @NonNull
    AllWeatherData loadWeather(Double latitude, Double longitude) throws IOException;
}
