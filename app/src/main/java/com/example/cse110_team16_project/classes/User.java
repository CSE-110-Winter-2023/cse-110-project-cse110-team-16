package com.example.cse110_team16_project.classes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.SensorManager;
import android.location.Location;


import com.example.cse110_team16_project.PermissionHandler;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;


public class User implements iLocationEntity{
    Activity activity;
    private FusedLocationProviderClient fusedLocationClient;
    Location location = null;
    //SensorManager sm;

    @SuppressLint("MissingPermission")
    User(Activity activity){
        this.activity = activity;
        PermissionHandler handler = new PermissionHandler(activity);
        if(handler.handleLocationPermission()){
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(activity, this::setLocation);
        }
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @SuppressLint("MissingPermission")
    public Location updateLocation(){
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(activity, (Location location) -> {
                        if(location != null) this.location = location;
                        });
        return this.location;
    }
}
