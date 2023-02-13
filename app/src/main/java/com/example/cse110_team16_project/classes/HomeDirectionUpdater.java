package com.example.cse110_team16_project.classes;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HomeDirectionUpdater {
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> future;

    private List<Home> homes;
    private List<Float> lastKnownHomeDirectionsFromUser;
    private User user;

    public HomeDirectionUpdater(Activity activity, @NonNull List<Home> homes, @NonNull User user){
        this.homes = homes;
        this.user = user;
        setAllDirectionsDefault();

        user.getCoordinates().observe((LifecycleOwner) activity, coordinates ->
                this.future = backgroundThreadExecutor.submit(() -> {
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
        for(int i = 0; i < homes.size(); i++){
            lastKnownHomeDirectionsFromUser.set(i,getHomeDirectionFromUser(user.getCoordinates().getValue(),
                    homes.get(i)));
        }
    }
    public Float getHomeDirectionFromUser(Coordinates userCoordinates, Home home){
        return userCoordinates.bearingTo(home.getCoordinates());
    }
}
