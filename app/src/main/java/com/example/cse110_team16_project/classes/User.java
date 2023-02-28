package com.example.cse110_team16_project.classes;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/*
User class is responsible for storing information about the User's location and information.
It is NOT responsible for updating the User position, this is currently handled in DeviceTracker.

coordinates is not allowed to be null and if a null is passed to setCoordinates, user will not update
 */
public class User extends CoordinateEntity {
    private final String name;

    public User(Coordinates coordinates, String name){
        super(coordinates);
        this.name = name;
    }
    public User(){
        this(new Coordinates(0,0), "");
    }

    public String getName() { return this.name;}
}
