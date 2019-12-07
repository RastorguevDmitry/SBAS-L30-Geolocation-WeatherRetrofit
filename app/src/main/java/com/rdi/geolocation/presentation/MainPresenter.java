package com.rdi.geolocation.presentation;


import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.rdi.geolocation.data.model.AllWeatherData;
import com.rdi.geolocation.domain.WetherRepository;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainPresenter {
    private WeakReference<IWeatherView> mWeatherWeakReference;
    private WetherRepository mWetherRepository;
    private final MutableLiveData<AllWeatherData> mWetherData = new MutableLiveData<>();


    private final Executor mExecutor = Executors.newSingleThreadExecutor();


    public MainPresenter(@NonNull IWeatherView weatherWeakReference,
                         @NonNull WetherRepository wetherRepository) {
        mWeatherWeakReference = new WeakReference<>(weatherWeakReference);
        mWetherRepository = wetherRepository;

    }

    public void detachView() {
        mWeatherWeakReference.clear();
    }


    public void setLocation(final Location location) {

        mExecutor.execute(() -> {
            try {
                AllWeatherData wetherData = mWetherRepository.loadWeather(location.getLatitude(), location.getLongitude());
                mWetherData.postValue(wetherData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @NonNull
    public MutableLiveData<AllWeatherData> getWetherData() {
        return mWetherData;
    }
}
