package com.example.cse110_team16_project.classes;

import android.location.Location;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;


public class User{
    Location location = null;
    float direction = 0.0f;
    //SensorManager sm;

    User(){}

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setDirection(float direction){
        this.direction = direction;
    }

    public float getDirection(){
        return this.direction;
    }

}
