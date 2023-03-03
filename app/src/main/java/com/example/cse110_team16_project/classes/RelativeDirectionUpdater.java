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

public class RelativeDirectionUpdater{
    private final ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> future;
    private LiveData<List<SCLocation>> scLocations;
    private final MutableLiveData<List<Degrees>> lastKnownEntityDirectionsFromUser = new MutableLiveData<>();

    public RelativeDirectionUpdater(Activity activity, @NonNull LiveData<List<SCLocation>>  coordinateEntities,
                                    @NonNull LiveData<Coordinates> userCoordinates, @NonNull LiveData<Radians> userOrientation){
        this.scLocations =  coordinateEntities;
        setAllDirectionsDefault();

        userCoordinates.observe((LifecycleOwner) activity, coordinates ->
                this.future = backgroundThreadExecutor.submit(() -> {
                    updateAllEntityDirectionsFromUser(coordinates,userOrientation.getValue());
                    return null;
                })
        );
    }

    public RelativeDirectionUpdater(Activity activity,
                                    @NonNull LiveData<Coordinates> userCoordinates, @NonNull LiveData<Radians> userOrientation){
        this(activity, new MutableLiveData<>(),userCoordinates,userOrientation);
    }

    public void setAllDirectionsDefault(){
        List<SCLocation> entities = scLocations.getValue();
        List<Degrees> defaultDirections = new ArrayList<>(entities.size());
        for(int i = 0; i < entities.size(); i++){
            defaultDirections.add(new Degrees(0.0));
        }
        lastKnownEntityDirectionsFromUser.setValue(defaultDirections);
    }

    public LiveData<List<SCLocation>> getScLocations(){
        return this.scLocations;
    }

    public void setScLocations(LiveData<List<SCLocation>> scLocations) {this.scLocations = scLocations;}

    public LiveData<List<Degrees>> getLastKnownEntityDirectionsFromUser(){
        return this.lastKnownEntityDirectionsFromUser;
    }
    public void updateAllEntityDirectionsFromUser(Coordinates userCoordinates, Radians userDirection){
        if(userCoordinates == null) return;

        List<Degrees> curDirections = getLastKnownEntityDirectionsFromUser().getValue();
        List<Degrees> newDirections = new ArrayList<>(curDirections.size());
        List<SCLocation> curEntities = scLocations.getValue();
        assert curDirections != null;

        for(int i = 0; i < curDirections.size(); i++){

            newDirections.add(Degrees.addDegrees(Converters.RadiansToDegrees(userDirection),
                    getEntityDirectionFromUser(userCoordinates,
                    curEntities.get(i),curDirections.get(i))));
        }
        lastKnownEntityDirectionsFromUser.postValue(newDirections);
    }
    public Degrees getEntityDirectionFromUser(Coordinates userCoordinates, SCLocation entity, Degrees lastKnown){
        if (entity.getCoordinates() == null)
            if(lastKnown == null) userCoordinates.bearingTo(new Coordinates(0,0));
            else return lastKnown;
        return userCoordinates.bearingTo(entity.getCoordinates());
    }

    public Future<Void>  getFuture() { return future; }
}
