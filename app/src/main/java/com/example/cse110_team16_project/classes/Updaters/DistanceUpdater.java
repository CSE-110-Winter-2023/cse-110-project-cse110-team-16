package com.example.cse110_team16_project.classes.Updaters;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cse110_team16_project.classes.Units.Meters;
import com.example.cse110_team16_project.classes.CoordinateClasses.Coordinates;
import com.example.cse110_team16_project.classes.CoordinateClasses.SCLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**lastKnownDistances.getValue() can contain null values or be null
 */
public class DistanceUpdater {
    private final ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<List<Meters>> lastKnownDistancesFromUser = new MutableLiveData<>(); //in meters

    public DistanceUpdater(Activity activity, @NonNull LiveData<List<SCLocation>> coordinateEntities,
                           @NonNull LiveData<Coordinates> userCoordinates) {

            userCoordinates.observe((LifecycleOwner) activity, coordinates ->
                    backgroundThreadExecutor.submit(() -> updateAllEntityDistancesFromUser(coordinateEntities.getValue(), coordinates))
            );

            coordinateEntities.observe((LifecycleOwner) activity, entityCoordinates ->
                    backgroundThreadExecutor.submit(() -> updateAllEntityDistancesFromUser(entityCoordinates, userCoordinates.getValue()))
            );

    }

    public LiveData<List<Meters>> getLastKnownEntityDistancesFromUser(){
        return this.lastKnownDistancesFromUser;
    }

    public void updateAllEntityDistancesFromUser(List<SCLocation> scLocations, Coordinates userCoordinates){
        if(userCoordinates == null ||  scLocations == null) return;

        List<Meters> curDistances = getLastKnownEntityDistancesFromUser().getValue();
        List<Meters> newDistances = new ArrayList<>(scLocations.size());

        for(int i = 0; i < scLocations.size(); i++){
            try {
                Meters newDistance = new Meters(getEntityDistanceFromUser(userCoordinates,
                        scLocations.get(i)));
                newDistances.add(newDistance);
            }
            catch (Exception e) {
                if(curDistances != null){
                    newDistances.add(curDistances.get(i));
                }
                else newDistances.add(null);
            }
        }
        lastKnownDistancesFromUser.postValue(newDistances);
    }
    public Double getEntityDistanceFromUser(Coordinates userCoordinates, SCLocation entity){
        if (entity.getCoordinates() == null || userCoordinates == null)
            return null;
        return userCoordinates.distanceTo(entity.getCoordinates());
    }

}

