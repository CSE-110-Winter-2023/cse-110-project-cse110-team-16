package com.example.cse110_team16_project.classes;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.annotation.SuppressLint;
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
    private final Activity activity;

    private MutableLiveData<Location> location;

    private final LocationManager locationManager;

    public static LocationService singleton(Activity activity) {
        if(instance == null){
            instance = new LocationService(activity);
        }
        return instance;
    }

    protected LocationService(Activity activity) {
        this.location = new MutableLiveData<>();
        this.activity = activity;
        this.locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        this.registerLocationListener();
    }

    protected void registerLocationListener() {
        if(ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED){
            throw new IllegalStateException("App needs location permission to get latest location");
        }

        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0,10,this);
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
