package com.example.cse110_team16_project.classes;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.LocationManager;

import androidx.lifecycle.LifecycleOwner;

import java.util.ArrayList;
import java.util.List;

public class LocationEntityTracker {
    Activity activity;
    User user = new User();
    List<Home> homes;
    List<Float> lastKnownDirection;
    private LocationService locationService;
    private OrientationService orientationService;

    public LocationEntityTracker(Activity activity, ArrayList<Home> homes){
        this.activity = activity;
        this.homes = homes;
        lastKnownDirection = new ArrayList<>(homes.size());
        setAllDirectionsDefault();

        locationService = LocationService.singleton(activity);
        locationService.getLocation().observe((LifecycleOwner) activity, loc->{
            user.setLocation(loc);
        });

        orientationService = OrientationService.singleton(activity);
        orientationService.getOrientation().observe((LifecycleOwner) activity, azimuth -> {
            user.setDirection(azimuth);
        });
    }
    public void setAllDirectionsDefault(){
        for(int i = 0; i < lastKnownDirection.size(); i++){
            lastKnownDirection.set(i,0.0f);
        }
    }
    public void updateAllHomesDirectionFromUser(){
        if(user.getLocation() == null) return;
        for(int i = 0; i < homes.size(); i++){
            lastKnownDirection.set(i,getHomeDirectionFromUser(homes.get(i)));
        }
    }
    public Float getHomeDirectionFromUser(Home home){
        return user.getLocation().bearingTo(home.getLocation());
    }

    public void unregisterListeners(){
        orientationService.unregisterSensorListeners();
        locationService.unregisterLocationListener();
    }
}
