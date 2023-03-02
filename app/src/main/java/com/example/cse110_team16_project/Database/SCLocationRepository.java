package com.example.cse110_team16_project.Database;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.cse110_team16_project.classes.SCLocation;

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
     * @param title the title of the SCLocation
     * @return a LiveData object that will be updated when the SCLocation is updated locally or remotely.
     */
    public LiveData<SCLocation> getSynced(String title) {
        var SCLocation = new MediatorLiveData<SCLocation>();

        Observer<SCLocation> updateFromRemote = theirSCLocation -> {
            var ourSCLocation = SCLocation.getValue();
            if(theirSCLocation == null) return;
            if (ourSCLocation == null || ourSCLocation.getLastUpdated().compareTo(theirSCLocation.getLastUpdated()) < 0) {
                upsertLocal(theirSCLocation);
            }
        };

        // If we get a local update, pass it on.
        SCLocation.addSource(getLocal(title), SCLocation::postValue);
        // If we get a remote update, update the local version (triggering the above observer)
        SCLocation.addSource(getRemote(title), updateFromRemote);

        return SCLocation;
    }

    public void upsertSynced(SCLocation SCLocation) {
        upsertLocal(SCLocation);
        upsertRemote(SCLocation);
    }

    // Local Methods
    // =============

    public LiveData<SCLocation> getLocal(String public_code) {
        return dao.get(public_code);
    }

    public LiveData<List<SCLocation>> getAllLocal() {
        return dao.getAll();
    }

    public void upsertLocal(SCLocation SCLocation) {
        //SCLocation.updatedAt = System.currentTimeMillis()/1000;
        dao.upsert(SCLocation);
    }

    public void deleteLocal(SCLocation SCLocation) {
        dao.delete(SCLocation);
    }

    public boolean existsLocal(String public_code) {
        return dao.exists(public_code);
    }

    // Remote Methods
    // ==============

    public LiveData<SCLocation> getRemote(String title) {
        // TODO: Implement getRemote!
        // TODO: Set up polling background thread (MutableLiveData?)
        // TODO: Refer to TimerService from https://github.com/DylanLukes/CSE-110-WI23-Demo5-V2.

        // Start by fetching the SCLocation from the server _once_ and feeding it into MutableLiveData.
        // Then, set up a background thread that will poll the server every 3 seconds.

        // You may (but don't have to) want to cache the LiveData's for each title, so that
        // you don't create a new polling thread every time you call getRemote with the same title.
        // You don't need to worry about killing background threads.


        MutableLiveData<SCLocation> SCLocation = new MutableLiveData<>(null);

        var executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            SCLocation.postValue(api.getSCLocation(title));
        },0,3, TimeUnit.SECONDS);

        return SCLocation;
    }

    public void upsertRemote(SCLocation SCLocation) {
        // TODO: Implement upsertRemote!
        Executors.newSingleThreadExecutor().submit(() -> api.putSCLocation(SCLocation));
    }
}
