package com.example.cse110_team16_project.classes;

import android.location.Location;


import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

/*
User class is responsible for storing information about the User's location and information.
It is NOT responsible for updating the User position, this is currently handled in UserTracker.
coordinates is not allowed to be null
 */
public class User{
    private MutableLiveData<Coordinates> coordinates;

    private MutableLiveData<Float> direction;

    public User(){
        coordinates = new MutableLiveData<>(new Coordinates(0,0)); //Null Island
        direction = new MutableLiveData<>(0.0f);
    }

    public void setCoordinates(@NonNull Coordinates coordinates) {
        this.coordinates.postValue(coordinates);
    }

    public LiveData<Coordinates> getCoordinates() {
        return coordinates;
    }

    //should send a value in degrees
    public void setDirection(float direction){
        this.direction.postValue(direction);
    }

    public LiveData<Float> getDirection(){
        return this.direction;
    }

}
