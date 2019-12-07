package com.rdi.geolocation.presentation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.rdi.geolocation.GeolocationServises.GeolocationServices;
import com.rdi.geolocation.R;
import com.rdi.geolocation.data.model.WetherDataMain;
import com.rdi.geolocation.domain.WetherRepository;

import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements IWeatherView {

    private static final int REQUEST_CODE = 101;
    private static final String TAG = "MainActivity";

    private static final int REQUEST_CHECK_SETTINGS = 102;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private LocationCallback mLocationCallback = new MainLocationCallback();

    private Geocoder mGeocoder;

    private MainPresenter mMainPresenter;

    private TextView mTextCurrentCity;


    private TextView mTextCurrentTemperatyre;
    private TextView mTextCurrentHumodity;
    private TextView mTextCurrentPresure;
    private TextView mTextCurrentMaxTemp;
    private TextView mTextCurrentMinTemp;


    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    WetherRepository mWetherRepository = new WetherRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGeocoder = new Geocoder(this, Locale.getDefault());

        initViews();
        providePresenter();
        addLiveData();
    }

    private void initViews() {
        mTextCurrentCity = findViewById(R.id.text_current_city);

        mTextCurrentTemperatyre = findViewById(R.id.text_current_temperature);
        mTextCurrentHumodity = findViewById(R.id.text_current_humidity);
        mTextCurrentPresure = findViewById(R.id.text_current_pressure);
        mTextCurrentMaxTemp = findViewById(R.id.text_current_temp_max);
        mTextCurrentMinTemp = findViewById(R.id.text_current_temp_min);
    }


    private void providePresenter() {
        mMainPresenter = new MainPresenter(this, mWetherRepository);


    }

    private void addLiveData() {
        GeolocationServices geolocationServices = new GeolocationServices(mGeocoder);
        geolocationServices.getTextCurrentAdress().observe(this, textCurrentCity ->
                mTextCurrentCity.setText(textCurrentCity)
        );

        mMainPresenter.getWetherData().observe(this, wetherData ->
        {
            WetherDataMain wetherDataMain = wetherData.getWetherDataMain();
            mTextCurrentTemperatyre.setText(String.valueOf(wetherDataMain.getTemp()));
            mTextCurrentHumodity.setText(String.valueOf(wetherDataMain.getHumidity()));
            mTextCurrentPresure.setText(String.valueOf(wetherDataMain.getPressure()));
            mTextCurrentMaxTemp.setText(String.valueOf(wetherDataMain.getTempMax()));
            mTextCurrentMinTemp.setText(String.valueOf(wetherDataMain.getTempMin()));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkGooglePlayServices();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mFusedLocationProviderClient != null) {
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMainPresenter.detachView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 1) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    startLocationService();
                } else {
                    finish();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        startLocationService();
                        break;
                    case Activity.RESULT_CANCELED:
                        finish();
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    private void checkGooglePlayServices() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int statusCode = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if (statusCode != ConnectionResult.SUCCESS) {
            Dialog errorDialog = googleApiAvailability.getErrorDialog(this, statusCode,
                    0, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            finish();
                        }
                    });

            errorDialog.show();
        } else {
            checkPermission();
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        } else {
            checkDeviceSettings();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_CODE);
    }

    private void checkDeviceSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(getLocationRequest());

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.d(TAG, "onSuccess() called with: locationSettingsResponse = [" + locationSettingsResponse + "]");

                startLocationService();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        Log.d(TAG, "onFailure() called with: e = [" + e + "]");
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void startLocationService() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mFusedLocationProviderClient.requestLocationUpdates(getLocationRequest(), mLocationCallback, null);
    }

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000000L);
        locationRequest.setFastestInterval(5000L);
        locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);

        return locationRequest;
    }

    private class MainLocationCallback extends LocationCallback {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Log.d(TAG, "onLocationResult() called with: locationResult = [" + locationResult + "]");

            if (locationResult == null) {
                return;
            }

            Location location = locationResult.getLocations().get(0);
            Log.i(TAG, "Location from LocationResuls = " + location);
            mMainPresenter.setLocation(location);

            GeolocationServices.GeocodingAsyncTask geocodingAsyncTask = new GeolocationServices.GeocodingAsyncTask(mGeocoder);
            geocodingAsyncTask.execute(location);
        }
    }


}
