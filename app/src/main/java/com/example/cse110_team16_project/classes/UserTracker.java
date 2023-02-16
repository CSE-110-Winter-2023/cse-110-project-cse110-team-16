package com.example.cse110_team16_project.classes;

import android.app.Activity;

import androidx.lifecycle.LifecycleOwner;

import com.example.cse110_team16_project.Room.Converters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class UserTracker {
    private final ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();

    public static final int UPDATE_TIME = 200;

    Activity activity;

    private final LocationService locationService;
    private final OrientationService orientationService;

    //TODO: Dependency Inversion on Services, but not much to test on this class
    public UserTracker(Activity activity, User user){
        this.activity = activity;

        locationService = LocationService.singleton(activity, UPDATE_TIME);
        locationService.getLocation().observe((LifecycleOwner) activity, loc->
                backgroundThreadExecutor.submit(()-> {
                    if(loc != null) {
                        user.setCoordinates(Converters.LocationToCoordinates(loc));
                    }
                return null;
                }
        ));

        orientationService = OrientationService.singleton(activity);
        orientationService.getOrientation().observe((LifecycleOwner) activity, azimuth ->
                backgroundThreadExecutor.submit(()-> {
                    user.setDirection((float)((Math.toDegrees(azimuth)+360)%360));
                    return null;
                }
        ));
    }

    public void registerListeners(){
        orientationService.registerSensorListeners();
        locationService.registerLocationListener(activity, UPDATE_TIME);
    }
    public void unregisterListeners(){
        orientationService.unregisterSensorListeners();
        locationService.unregisterLocationListener();
    }

    public void mockUserDirection(float mockDirection){
        orientationService.setMockOrientationSource(mockDirection);
    }
}
