package com.example.cse110_team16_project.classes;

import android.app.Activity;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cse110_team16_project.Database.SCLocationRepository;

public class UserLocationSynch {
    SCLocation location;
    MutableLiveData<SCLocation> locationLive = new MutableLiveData<SCLocation>(null);

    public UserLocationSynch(LiveData<Coordinates> userCoords, SCLocation user, String private_code,
                      Activity activity, SCLocationRepository repo){

        location = user;
        repo.updateSCLocationLive(locationLive,private_code);
        userCoords.observe((LifecycleOwner) activity,(potentialNullCoords) -> {
            if(potentialNullCoords != null) {
                userCoords.removeObservers((LifecycleOwner) activity);
                userCoords.observe((LifecycleOwner) activity, (coords) -> {
                    location.setCoordinates(coords);
                    locationLive.postValue(location);
                });
            }
        });
    }
}
