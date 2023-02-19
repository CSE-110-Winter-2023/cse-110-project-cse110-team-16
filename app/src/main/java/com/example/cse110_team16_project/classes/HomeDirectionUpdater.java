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
    private final ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> future;
    private List<Home> homes;
    private final MutableLiveData<List<Float>> lastKnownHomeDirectionsFromUser;
    private final User user;

    public HomeDirectionUpdater(Activity activity, @NonNull List<Home> homes, @NonNull User user){
        this.homes = homes;
        this.user = user;
        lastKnownHomeDirectionsFromUser = new MutableLiveData<>();
        setAllDirectionsDefault();

        user.getCoordinates().observe((LifecycleOwner) activity, coordinates ->
                this.future = backgroundThreadExecutor.submit(() -> {
                    updateAllHomesDirectionFromUser();
                    return null;
                })
        );

    }
    public void setAllDirectionsDefault(){
        List<Float> defaultDirections = new ArrayList<>(homes.size());
        for(int i = 0; i < homes.size(); i++){
            defaultDirections.add(i,0.0f);
        }
        lastKnownHomeDirectionsFromUser.setValue(defaultDirections);
    }

    public List<Home> getHomes(){
        return this.homes;
    }

    public void setHomes(List<Home> homes) {this.homes = homes;}

    public LiveData<List<Float>> getLastKnownHomeDirectionsFromUser(){
        return this.lastKnownHomeDirectionsFromUser;
    }
    public void updateAllHomesDirectionFromUser(){
        Coordinates userCoordinates = user.getCoordinates().getValue();
        if(userCoordinates == null) return;
        List<Float> newDirections = new ArrayList<>(homes.size());

        for(int i = 0; i < homes.size(); i++){
            newDirections.add(i,getHomeDirectionFromUser(userCoordinates,
                    homes.get(i)));
        }
        lastKnownHomeDirectionsFromUser.postValue(newDirections);
    }
    public Float getHomeDirectionFromUser(Coordinates userCoordinates, Home home){
        return userCoordinates.bearingTo(home.getCoordinates());
    }

    public Future<Void>  getFuture() { return future; }
}
