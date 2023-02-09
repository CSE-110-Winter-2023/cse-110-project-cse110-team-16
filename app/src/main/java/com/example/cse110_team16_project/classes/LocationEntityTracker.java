package com.example.cse110_team16_project.classes;

import android.app.Activity;

import java.util.ArrayList;

public class LocationEntityTracker {
    Activity activity;

    User user;
    ArrayList<Home> homes;
    ArrayList<Float> lastKnownDirection;

    public LocationEntityTracker(Activity activity, User user, ArrayList<Home> homes){
        this.activity = activity;
        this.user = user;
        this.homes = homes;
        lastKnownDirection = getAllHomesDirectionFromUser();
    }

    public ArrayList<Float> getAllHomesDirectionFromUser(){
        ArrayList<Float> directions = new ArrayList<>(homes.size());
        for(int i = 0; i < homes.size(); i++){
            directions.add(getHomeDirectionFromUser(homes.get(i)));
        }
        return directions;
    }
    public Float getHomeDirectionFromUser(Home home){
        if(user.getLocation() == null) return 0.0f;
        return user.getLocation().bearingTo(home.getLocation());
    }
}
