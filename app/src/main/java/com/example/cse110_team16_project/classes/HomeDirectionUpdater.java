package com.example.cse110_team16_project.classes;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeDirectionUpdater {
    private final ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();

    private List<Home> homes;
    private List<Float> lastKnownHomeDirectionsFromUser;
    private final User user;

    public HomeDirectionUpdater(Activity activity, @NonNull List<Home> homes, @NonNull User user){
        this.homes = homes;
        this.user = user;
        setAllDirectionsDefault();

        user.getCoordinates().observe((LifecycleOwner) activity, coordinates ->
                backgroundThreadExecutor.submit(() -> {
                    updateAllHomesDirectionFromUser();
                    return null;
                })
        );

    }
    public void setAllDirectionsDefault(){
        lastKnownHomeDirectionsFromUser = new ArrayList<>(homes.size());
        for(int i = 0; i < homes.size(); i++){
            lastKnownHomeDirectionsFromUser.add(i,0.0f);
        }
    }

    public List<Home> getHomes(){
        return this.homes;
    }

    public void setHomes(List<Home> homes) {this.homes = homes;}

    public List<Float> getLastKnownHomeDirectionsFromUser(){
        return this.lastKnownHomeDirectionsFromUser;
    }
    public void updateAllHomesDirectionFromUser(){
        Coordinates userCoordinates = user.getCoordinates().getValue();
        if(userCoordinates == null) return;
        for(int i = 0; i < homes.size(); i++){
            lastKnownHomeDirectionsFromUser.set(i,getHomeDirectionFromUser(userCoordinates,
                    homes.get(i)));
        }
    }
    public Float getHomeDirectionFromUser(Coordinates userCoordinates, Home home){
        return userCoordinates.bearingTo(home.getCoordinates());
    }
}
