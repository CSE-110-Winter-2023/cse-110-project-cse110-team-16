package com.example.cse110_team16_project.classes;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cse110_team16_project.Units.Degrees;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**lastKnownEntityDirectionFromUser.getValue() can contain null values or be null
 */
public class AbsoluteDirectionUpdater {
    private final ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> future;

    private final MutableLiveData<List<Degrees>> lastKnownEntityDirectionsFromUser = new MutableLiveData<>();

    public AbsoluteDirectionUpdater(Activity activity, @NonNull LiveData<List<SCLocation>>  coordinateEntities,
                                    @NonNull LiveData<Coordinates> userCoordinates){

            userCoordinates.observe((LifecycleOwner) activity, coordinates ->
                    this.future = backgroundThreadExecutor.submit(() -> {
                        updateAllEntityDirectionsFromUser(coordinateEntities.getValue(), coordinates);
                        return null;
                    })
            );

            coordinateEntities.observe((LifecycleOwner) activity, entityCoordinates ->
                    this.future = backgroundThreadExecutor.submit(() -> {
                        updateAllEntityDirectionsFromUser(entityCoordinates, userCoordinates.getValue());
                        return null;
                    })
            );
    }

    public AbsoluteDirectionUpdater(Activity activity,
                                    @NonNull LiveData<Coordinates> userCoordinates){
        this(activity, new MutableLiveData<>(),userCoordinates);
    }


    public LiveData<List<Degrees>> getLastKnownEntityDirectionsFromUser(){
        return this.lastKnownEntityDirectionsFromUser;
    }
    public void updateAllEntityDirectionsFromUser(List<SCLocation> scLocations, Coordinates userCoordinates){
        if(userCoordinates == null || scLocations == null) return;

        List<Degrees> curDirections = getLastKnownEntityDirectionsFromUser().getValue();

        List<Degrees> newDirections = new ArrayList<>(scLocations.size());

        for(int i = 0; i < scLocations.size(); i++){
            Degrees newDirection = getEntityDirectionFromUser(userCoordinates,
                    scLocations.get(i));
            if(curDirections != null && newDirection == null) {
                newDirections.add(curDirections.get(i));
            }
            else newDirections.add(newDirection);
        }
        lastKnownEntityDirectionsFromUser.postValue(newDirections);
    }
    public Degrees getEntityDirectionFromUser(Coordinates userCoordinates, SCLocation entity){
        if (entity == null || entity.getCoordinates() == null)
            return null;
        return userCoordinates.bearingTo(entity.getCoordinates());
    }

    public Future<Void>  getFuture() { return future; }
}
