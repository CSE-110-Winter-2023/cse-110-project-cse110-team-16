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

/**lastKnownDistances.getValue() can contain null values or be null
 */
public class DistanceUpdater {
    private final ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> future;

    private final MutableLiveData<List<Double>> lastKnownDistancesFromUser = new MutableLiveData<>(); //in meters

    public DistanceUpdater(Activity activity, @NonNull LiveData<List<SCLocation>> coordinateEntities,
                           @NonNull LiveData<Coordinates> userCoordinates) {

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
    }

    public LiveData<List<Double>> getLastKnownEntityDistancesFromUser(){
        return this.lastKnownDistancesFromUser;
    }
    public void updateAllEntityDistancesFromUser(List<SCLocation> scLocations, Coordinates userCoordinates){
        if(userCoordinates == null ||  scLocations == null) return;

        List<Double> curDistances = getLastKnownEntityDistancesFromUser().getValue();
        List<Double> newDistances = new ArrayList<>(scLocations.size());

        for(int i = 0; i < scLocations.size(); i++){
            Double newDistance = getEntityDistanceFromUser(userCoordinates,
                    scLocations.get(i));
            if(curDistances != null && newDistance == null){
                newDistances.add(curDistances.get(i));
            }
            else newDistances.add(newDistance);
        }
        lastKnownDistancesFromUser.postValue(newDistances);
    }
    public Double getEntityDistanceFromUser(Coordinates userCoordinates, SCLocation entity){
        if (entity.getCoordinates() == null || userCoordinates == null)
            return null;
        return userCoordinates.distanceTo(entity.getCoordinates());
    }

    public Future<Void>  getFuture() { return future; }
}

