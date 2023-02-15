package com.example.cse110_team16_project.classes;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class LocationService implements LocationListener {

    private static LocationService instance;

    private MutableLiveData<Location> location;

    private final LocationManager locationManager;

    public static LocationService singleton(Activity activity, int minTime) {
        if(instance == null){
            instance = new LocationService(activity, minTime);
        }
        return instance;
    }

    protected LocationService(Activity activity, int updateTime) {
        this.location = new MutableLiveData<>();
        this.locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        this.registerLocationListener(activity, updateTime);
    }

    protected void registerLocationListener(Activity activity, int minTime) {
        if(ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED){
            throw new IllegalStateException("App needs location permission to get latest location");
        }

        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                minTime,10,this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location){
        this.location.postValue(location);
    }

    public void unregisterLocationListener() {
        locationManager.removeUpdates(this);
    }

    public LiveData<Location> getLocation() {
        return this.location;
    }

    public void setMockOrientationSource(MutableLiveData<Location> mockDataSource){
        unregisterLocationListener();
        this.location = mockDataSource;
    }
}
