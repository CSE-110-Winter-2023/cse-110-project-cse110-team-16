package com.example.cse110_team16_project.classes;

import android.app.Activity;
import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;


public class LocationEntityTracker {
    private static final String TAG = LocationEntityTracker.class.getSimpleName();
    //FOR DEBUGGING

    Activity activity;
    private User user = new User();
    private List<Home> homes;
    private List<Float> lastKnownDirectionHomesFromUser;
    private LocationService locationService;
    private OrientationService orientationService;

    public LocationEntityTracker(Activity activity, List<Home> homes){
        this.activity = activity;
        this.homes = homes;
        lastKnownDirectionHomesFromUser = new ArrayList<>(homes.size());
        setAllDirectionsDefault();

        locationService = LocationService.singleton(activity);
        locationService.getLocation().observe((LifecycleOwner) activity, loc->{
            user.setCoordinates(new Coordinates(loc));
            updateAllHomesDirectionFromUser();
        });

        orientationService = OrientationService.singleton(activity);
        orientationService.getOrientation().observe((LifecycleOwner) activity, azimuth -> {
            user.setDirection((float)((Math.toDegrees(azimuth)+360)%360));
            //Log.d(TAG,Float.toString(user.getDirection().getValue())); //DEBUG
            updateAllHomesDirectionFromUser();
        });
    }
    public void setAllDirectionsDefault(){
        for(int i = 0; i < lastKnownDirectionHomesFromUser.size(); i++){
            lastKnownDirectionHomesFromUser.set(i,0.0f);
        }
    }

    public List<Home> getHomes(){
        return this.homes;
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
    public List<Float> getLastKnownDirectionHomesFromUser(){
        return this.lastKnownDirectionHomesFromUser;
    }
    public void updateAllHomesDirectionFromUser(){
        if(user.getCoordinates() == null) return;
        for(int i = 0; i < homes.size(); i++){
            lastKnownDirectionHomesFromUser.set(i,getHomeDirectionFromUser(homes.get(i)));
        }
    }
    public Float getHomeDirectionFromUser(Home home){
        return user.getCoordinates().getValue().bearingTo(home.getLocation());
    }

    public void registerListeners(){
        orientationService.registerSensorListeners();
        locationService.registerLocationListener();
    }
    public void unregisterListeners(){
        orientationService.unregisterSensorListeners();
        locationService.unregisterLocationListener();
    }
}
