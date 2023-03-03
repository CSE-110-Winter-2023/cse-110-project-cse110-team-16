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

public class DistanceUpdater {
    private final ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> future;

    private final MutableLiveData<List<Double>> lastKnownDistancesFromUser = new MutableLiveData<>(); //in meters

    public DistanceUpdater(Activity activity, @NonNull LiveData<List<SCLocation>> coordinateEntities,
                           @NonNull LiveData<Coordinates> userCoordinates) {
        coordinateEntities.observe((LifecycleOwner) activity, locations -> {
            //TODO: Might (probably) conflict with AbsoluteDirectionUpdater removeObservers
            coordinateEntities.removeObservers((LifecycleOwner) activity);
            setAllDistancesDefault(locations);

            userCoordinates.observe((LifecycleOwner) activity, coordinates ->
                    this.future = backgroundThreadExecutor.submit(() -> {
                        updateAllEntityDistancesFromUser(coordinateEntities.getValue(), coordinates);
                        return null;
                    })
            );

            coordinateEntities.observe((LifecycleOwner) activity, entityCoordinates ->
                    this.future = backgroundThreadExecutor.submit(() -> {
                        updateAllEntityDistancesFromUser(entityCoordinates, userCoordinates.getValue());
                        return null;
                    })
            );
        });
    }
    public void setAllDistancesDefault(List<SCLocation> entities){
        List<Double> defaultDistances = new ArrayList<>(entities.size());
        for(int i = 0; i < entities.size(); i++){
            defaultDistances.add(10000.0); //TODO: temp large number so that they all show up at the edge of compass
        }
        lastKnownDistancesFromUser.setValue(defaultDistances);
    }

    public LiveData<List<Double>> getLastKnownEntityDistancesFromUser(){
        return this.lastKnownDistancesFromUser;
    }
    public void updateAllEntityDistancesFromUser(List<SCLocation> scLocations, Coordinates userCoordinates){
        if(userCoordinates == null) return;

        List<Double> curDistances = getLastKnownEntityDistancesFromUser().getValue();
        assert curDistances != null;
        List<Double> newDistances = new ArrayList<>(curDistances.size());

        for(int i = 0; i < curDistances.size(); i++){

            newDistances.add(getEntityDistanceFromUser(userCoordinates,
                    scLocations.get(i),curDistances.get(i)));
        }
        lastKnownDistancesFromUser.postValue(newDistances);
    }
    public Double getEntityDistanceFromUser(Coordinates userCoordinates, SCLocation entity, Double lastKnown){
        if (entity.getCoordinates() == null)
            return lastKnown;
        return userCoordinates.distanceTo(entity.getCoordinates());
    }

    public Future<Void>  getFuture() { return future; }
}

