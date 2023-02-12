package com.example.cse110_team16_project.classes;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class UserTracker {
    private static final String TAG = UserTracker.class.getSimpleName();
    //FOR DEBUGGING

    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> future;

    private HomeDirectionUpdater homeDirectionUpdater;

    public static final int UPDATE_TIME = 200;

    Activity activity;
    private final User user;

    private final LocationService locationService;
    private final OrientationService orientationService;

    //TODO: Dependency Inversion on Services, but not much to test on this class
    public UserTracker(Activity activity, User user){
        this.activity = activity;
        this.user = user;



        locationService = LocationService.singleton(activity, UPDATE_TIME);
        locationService.getLocation().observe((LifecycleOwner) activity, loc->{
                this.future = backgroundThreadExecutor.submit(()-> {
                    user.setCoordinates(new Coordinates(loc));
                    return null;
                });
        });

        orientationService = OrientationService.singleton(activity);
        orientationService.getOrientation().observe((LifecycleOwner) activity, azimuth -> {
                this.future = backgroundThreadExecutor.submit(()-> {
                    user.setDirection((float)((Math.toDegrees(azimuth)+360)%360));
                    //Log.d(TAG,Float.toString(user.getDirection().getValue())); //DEBUG
                    return null;
                });
        });
    }




    protected Activity getActivity(){
        return this.activity;
    }

    public LiveData<Coordinates> getUserCoordinates(){
        return user.getCoordinates();
    }

    public LiveData<Float> getUserDirection(){
        return user.getDirection();
    }


    public void registerListeners(){
        orientationService.registerSensorListeners();
        locationService.registerLocationListener(UPDATE_TIME);
    }
    public void unregisterListeners(){
        orientationService.unregisterSensorListeners();
        locationService.unregisterLocationListener();
    }
}
