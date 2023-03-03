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

public class AbsoluteDirectionUpdater {
    private final ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> future;

    private final MutableLiveData<List<Degrees>> lastKnownEntityDirectionsFromUser = new MutableLiveData<>();

    public AbsoluteDirectionUpdater(Activity activity, @NonNull LiveData<List<SCLocation>>  coordinateEntities,
                                    @NonNull LiveData<Coordinates> userCoordinates){

        coordinateEntities.observe((LifecycleOwner) activity, locations -> {
            coordinateEntities.removeObservers((LifecycleOwner) activity);
            setAllDirectionsDefault(locations);

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
        });

    }

    public AbsoluteDirectionUpdater(Activity activity,
                                    @NonNull LiveData<Coordinates> userCoordinates){
        this(activity, new MutableLiveData<>(),userCoordinates);
    }

    public void setAllDirectionsDefault(List<SCLocation> entities){
        List<Degrees> defaultDirections = new ArrayList<>(entities.size());
        for(int i = 0; i < entities.size(); i++){
            defaultDirections.add(new Degrees(0.0));
        }
        lastKnownEntityDirectionsFromUser.setValue(defaultDirections);
    }

    public LiveData<List<Degrees>> getLastKnownEntityDirectionsFromUser(){
        return this.lastKnownEntityDirectionsFromUser;
    }
    public void updateAllEntityDirectionsFromUser(List<SCLocation> scLocations, Coordinates userCoordinates){
        if(userCoordinates == null) return;

        List<Degrees> curDirections = getLastKnownEntityDirectionsFromUser().getValue();
        assert curDirections != null;
        List<Degrees> newDirections = new ArrayList<>(curDirections.size());

        for(int i = 0; i < curDirections.size(); i++){

            newDirections.add(getEntityDirectionFromUser(userCoordinates,
                    scLocations.get(i),curDirections.get(i)));
        }
        lastKnownEntityDirectionsFromUser.postValue(newDirections);
    }
    public Degrees getEntityDirectionFromUser(Coordinates userCoordinates, SCLocation entity, Degrees lastKnown){
        if (entity.getCoordinates() == null)
            if(lastKnown == null) userCoordinates.bearingTo(new Coordinates(0,0)); //TODO: probably doesn't fully close GPS connection issues
            else return lastKnown;
        return userCoordinates.bearingTo(entity.getCoordinates());
    }

    public Future<Void>  getFuture() { return future; }
}
