package com.rdi.geolocation.GeolocationServises;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeolocationServices {
    private static final String TAG = "GeolocationServices";

    Geocoder mGeocoder;

    private static final MutableLiveData<String> mTextCurrentAdress = new MutableLiveData<>();


    public GeolocationServices(Geocoder geocoder) {
        mGeocoder = geocoder;
    }

    public static class GeocodingAsyncTask extends AsyncTask<Location, Void, List<Address>> {

        private final Geocoder mGeocoder;

        public GeocodingAsyncTask(Geocoder geocoder) {
            mGeocoder = geocoder;
        }

        @Override
        protected List<Address> doInBackground(Location... locations) {
            Location location = locations[0];

            try {
                List<Address> addressList = mGeocoder.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 1);

                return addressList;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return new ArrayList<>();
        }

        @Override
        protected void onPostExecute(List<Address> addressList) {
            super.onPostExecute(addressList);

            for (Address address : addressList) {
                Log.i(TAG, "Address = " + address);
                mTextCurrentAdress.setValue(address.getAddressLine(0));
            }
        }
    }

    public MutableLiveData<String> getTextCurrentAdress() {
        return mTextCurrentAdress;
    }
}
