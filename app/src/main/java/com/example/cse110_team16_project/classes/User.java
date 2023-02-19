package com.example.cse110_team16_project.classes;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/*
User class is responsible for storing information about the User's location and information.
It is NOT responsible for updating the User position, this is currently handled in UserTracker.

coordinates is not allowed to be null and if a null is passed to setCoordinates, user will not update
 */
public class User{
    private final MutableLiveData<Coordinates> coordinates;
    private final MutableLiveData<Float> direction;

    public User(){
        coordinates = new MutableLiveData<>(new Coordinates(0,0)); //Null Island
        direction = new MutableLiveData<>(0.0f);
    }

    public void setCoordinates(Coordinates coordinates) {
        if(coordinates != null) this.coordinates.postValue(coordinates);
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
