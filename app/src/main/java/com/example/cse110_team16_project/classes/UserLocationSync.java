package com.example.cse110_team16_project.classes;

import android.app.Activity;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cse110_team16_project.Database.SCLocationRepository;
import com.example.cse110_team16_project.classes.CoordinateClasses.Coordinates;
import com.example.cse110_team16_project.classes.CoordinateClasses.SCLocation;

public class UserLocationSync {
    SCLocation location;
    MutableLiveData<SCLocation> locationLive = new MutableLiveData<>();

    public UserLocationSync(LiveData<Coordinates> userCoords, SCLocation user, String private_code,
                            Activity activity, SCLocationRepository repo){

        location = user;
        repo.updateSCLocationLive(locationLive,private_code);
                userCoords.observe((LifecycleOwner) activity, (coords) -> {
                    location.setCoordinates(coords);
                    locationLive.postValue(location);
                });

    }
}
