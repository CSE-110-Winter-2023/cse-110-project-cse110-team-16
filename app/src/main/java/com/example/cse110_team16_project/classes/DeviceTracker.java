package com.example.cse110_team16_project.classes;

import android.app.Activity;
import android.location.Location;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cse110_team16_project.Room.Converters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DeviceTracker {
    private final ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();

    private static final int UPDATE_TIME = 200;
    private static final int UPDATE_MIN_METERS = 20;

    Activity activity;

    private final LocationService locationService;
    private final OrientationService orientationService;

    private final MutableLiveData<Coordinates> coordinates = new MutableLiveData<>();

    public DeviceTracker(Activity activity){
        this(activity, LocationService.singleton(activity,UPDATE_TIME, UPDATE_MIN_METERS),
                OrientationService.singleton(activity));
    }

    public DeviceTracker(Activity activity, LocationService locationService,
                         OrientationService orientationService) {
        this.activity = activity;
        this.locationService = locationService;
        this.orientationService = orientationService;
    }

    public void registerListeners(){
        orientationService.registerSensorListeners();
        locationService.registerLocationListener(activity, UPDATE_TIME, UPDATE_MIN_METERS);
    }
    public void unregisterListeners(){
        orientationService.unregisterSensorListeners();
        locationService.unregisterLocationListener();
    }

    //disables mocking of userOrientation if param < 0f
    public void mockUserDirection(Degrees mockDirection){
        if(mockDirection.getDegrees() < 0) orientationService.disableMockMode();
        else orientationService.setMockOrientationSource(mockDirection);
    }
    public void disableMockUserDirection(){
        orientationService.disableMockMode();
    }

    public LiveData<Radians> getOrientation() { return orientationService.getOrientation(); }

    //should use getCoordinates over this
    public LiveData<Location> getLocation() { return locationService.getLocation(); }

    public LiveData<Coordinates> getCoordinates() { return coordinates; }
}
