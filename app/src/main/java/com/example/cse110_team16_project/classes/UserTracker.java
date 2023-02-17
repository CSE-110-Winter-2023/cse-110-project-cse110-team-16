package com.example.cse110_team16_project.classes;

import android.app.Activity;

import androidx.lifecycle.LifecycleOwner;

import com.example.cse110_team16_project.Room.Converters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class UserTracker {
    private final ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();

    private static final int UPDATE_TIME = 200;
    private static final int UPDATE_MIN_METERS = 20;

    Activity activity;

    private final LocationService locationService;
    private final OrientationService orientationService;

    public UserTracker(Activity activity, User user){
        this(activity,user,LocationService.singleton(activity,UPDATE_TIME, UPDATE_MIN_METERS),
                OrientationService.singleton(activity));
    }

    public UserTracker(Activity activity, User user, LocationService locationService,
                       OrientationService orientationService) {
        this.activity = activity;

        this.locationService = locationService;
        this.locationService.getLocation().observe((LifecycleOwner) activity, loc->
                backgroundThreadExecutor.submit(()-> {
                            if(loc != null) {
                                user.setCoordinates(Converters.LocationToCoordinates(loc));
                            }
                            return null;
                        }
                ));

        this.orientationService = orientationService;
        this.orientationService.getOrientation().observe((LifecycleOwner) activity, azimuth ->
                backgroundThreadExecutor.submit(()-> {
                            user.setDirection((float)((Math.toDegrees(azimuth)+360)%360));
                            return null;
                        }
                ));
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
    public void mockUserDirection(float mockDirection){
        if(mockDirection < 0f) orientationService.disableMockMode();
        else orientationService.setMockOrientationSource(mockDirection);
    }
}
