package com.example.cse110_team16_project.classes;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class LocationEntityTracker {
    Activity activity;

    User user;
    List<Home> homes;
    List<Float> lastKnownDirection;

    public LocationEntityTracker(Activity activity, User user, ArrayList<Home> homes){
        this.activity = activity;
        this.user = user;
        this.homes = homes;
        lastKnownDirection = getAllHomesDirectionFromUser();
    }

    public List<Float> getAllHomesDirectionFromUser(){
        List<Float> directions = new ArrayList<>(homes.size());
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
