package com.example.cse110_team16_project.Database;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.cse110_team16_project.classes.SCLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SCLocationRepository {
    private final SCLocationDao dao;
    private final SCLocationAPI api = SCLocationAPI.provide();
    public SCLocationRepository(SCLocationDao dao) {
        this.dao = dao;
    }

    // Synced Methods
    // ==============

    /**
     * This is where the magic happens. This method will return a LiveData object that will be
     * updated when the SCLocation is updated either locally or remotely on the server. Our activities
     * however will only need to observe this one LiveData object, and don't need to care where
     * it comes from!
     *
     * This method will always prefer the newest version of the SCLocation.
     *
     * @param public_code the public_code of the SCLocation
     * @return a LiveData object that will be updated when the SCLocation is updated locally or remotely.
     */
    public LiveData<SCLocation> getSynced(String public_code) {
        var scLocation = new MediatorLiveData<SCLocation>();

        Observer<SCLocation> updateFromRemote = theirSCLocation -> {
            var ourSCLocation = scLocation.getValue();
            if(theirSCLocation == null) return;
            if (ourSCLocation == null || ourSCLocation.getLastUpdated().compareTo(theirSCLocation.getLastUpdated()) < 0) {
                upsertLocal(theirSCLocation);
            }
        };

        // If we get a local update, pass it on.
        scLocation.addSource(getLocal(public_code), scLocation::postValue);
        // If we get a remote update, update the local version (triggering the above observer)
        scLocation.addSource(getRemote(public_code), updateFromRemote);

        return scLocation;
    }

    public void upsertSynced(SCLocation scLocation) {
        upsertLocal(scLocation);
        upsertRemote(scLocation);
    }

    // Local Methods
    // =============

    public LiveData<SCLocation> getLocal(String public_code) {
        return dao.get(public_code);
    }

    public List<String> getLocalPublicCodes() { return dao.getAllPublicCodes();}
    public LiveData<List<SCLocation>> getAllLocal() {
        return dao.getAll();
    }

    public void upsertLocal(SCLocation scLocation) {
        //SCLocation.updatedAt = System.currentTimeMillis()/1000;
        dao.upsert(scLocation);
    }

    public void deleteLocal(SCLocation scLocation) {
        dao.delete(scLocation);
    }

    public boolean existsLocal(String public_code) {
        return dao.exists(public_code);
    }

    // Remote Methods
    // ==============

    public LiveData<List<SCLocation>> getRemote(List<String> public_codes){
        MutableLiveData<List<SCLocation>> locations = new MutableLiveData<>();

        var executor = Executors.newSingleThreadScheduledExecutor();

        executor.scheduleAtFixedRate(() -> {
            List<SCLocation> newLocations = new ArrayList<>();
            for(String public_code : public_codes) {
                newLocations.add(api.getSCLocation(public_code)); //TODO: null check?
            }
            locations.postValue(newLocations);
        },0,3, TimeUnit.SECONDS);

        return locations;
    }
    public LiveData<SCLocation> getRemote(String public_code) {
        // TODO: Implement getRemote!
        // TODO: Set up polling background thread (MutableLiveData?)
        // TODO: Refer to TimerService from https://github.com/DylanLukes/CSE-110-WI23-Demo5-V2.

        // Start by fetching the SCLocation from the server _once_ and feeding it into MutableLiveData.
        // Then, set up a background thread that will poll the server every 3 seconds.

        // You may (but don't have to) want to cache the LiveData's for each public_code, so that
        // you don't create a new polling thread every time you call getRemote with the same public_code.
        // You don't need to worry about killing background threads.


        MutableLiveData<SCLocation> scLocation = new MutableLiveData<>(null);

        var executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
           scLocation.postValue(api.getSCLocation(public_code));
        },0,3, TimeUnit.SECONDS);

        return scLocation;
    }

    public void upsertRemote(SCLocation scLocation) {
        // TODO: Implement upsertRemote!
        Executors.newSingleThreadExecutor().submit(() -> api.putSCLocation(scLocation));
    }
}
