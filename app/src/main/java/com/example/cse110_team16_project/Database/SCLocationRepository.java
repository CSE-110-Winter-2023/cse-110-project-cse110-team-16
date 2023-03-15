package com.example.cse110_team16_project.Database;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.cse110_team16_project.classes.CoordinateClasses.SCLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SCLocationRepository {

    public static final int LIVE_UPDATE_TIME_MS = 500;
    private final SCLocationDao dao;
    private final SCLocationAPI api = SCLocationAPI.provide();
    public SCLocationRepository(SCLocationDao dao) {
        this.dao = dao;
    }
    private final List<ScheduledFuture<?>> remoteUpdateThreads = new ArrayList<>();

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

    //likely unnecessary for this MS, just use getRemote
    public LiveData<SCLocation> getSynced(String public_code) {
        var scLocation = new MediatorLiveData<SCLocation>();

        Observer<SCLocation> updateFromRemote = theirSCLocation -> {
            var ourSCLocation = scLocation.getValue();
            if(theirSCLocation == null) return;
            if (ourSCLocation == null) {
                upsertLocal(theirSCLocation);
            }
        };

        // If we get a local update, pass it on.
        scLocation.addSource(getLocalLive(public_code), scLocation::postValue);
        // If we get a remote update, update the local version (triggering the above observer)
        scLocation.addSource(getRemoteLive(public_code), updateFromRemote);

        return scLocation;
    }

    /*
    public void upsertSynced(SCLocation scLocation, String private_code) {
        upsertLocal(scLocation);
        upsertRemote(scLocation, private_code);
    }
    */

    // Local Methods
    // =============
    public SCLocation getLocal(String public_code) {
        return dao.get(public_code);
    }
    public LiveData<SCLocation> getLocalLive(String public_code) {
        return dao.getLive(public_code);
    }

    public LiveData<List<SCLocation>> getAllLocalLive() {
        return dao.getAllLive();
    }

    public List<String> getLocalPublicCodes() { return dao.getAllPublicCodes();}

    public List<String> getLocalLabels() {return dao.getAllLabels();}

    //TODO: Reminder that dao.insert is used here and not dao.upsert
    public void upsertLocal(SCLocation scLocation) {
        dao.insert(scLocation);
    }

    public void deleteLocal(SCLocation scLocation) {
        dao.delete(scLocation);
    }

    public boolean existsLocal(String public_code) {
        return dao.exists(public_code);
    }
    public void deleteRemote(String public_code, String private_code) {
        Executors.newSingleThreadExecutor().submit(() -> api.deleteSCLocation(public_code, private_code));

    }
    public boolean existsRemote(String public_code) {
        try {
            return Executors.newSingleThreadExecutor().submit(() -> (api.getSCLocation(public_code) != null)).get();
        } catch (ExecutionException | InterruptedException e) {
            return false;
        }
    }
    // Remote Methods
    // ==============
    public SCLocation getRemote(String public_code){
        try {
            return Executors.newSingleThreadExecutor().submit(() -> (api.getSCLocation(public_code))).get();
        } catch (ExecutionException | InterruptedException e) {
            return null;
        }
    }

    public LiveData<SCLocation> getRemoteLive(String public_code) {

        // Start by fetching the SCLocation from the server _once_ and feeding it into MutableLiveData.
        // Then, set up a background thread that will poll the server every 3 seconds.

        // You may (but don't have to) want to cache the LiveData's for each public_code, so that
        // you don't create a new polling thread every time you call getRemote with the same public_code.
        // You don't need to worry about killing background threads.


        MutableLiveData<SCLocation> scLocation = new MutableLiveData<>();

        var executor = Executors.newSingleThreadScheduledExecutor();
        remoteUpdateThreads.add(
            executor.scheduleAtFixedRate(() -> scLocation.postValue(api.getSCLocation(public_code)),
                    0, LIVE_UPDATE_TIME, TimeUnit.MILLISECONDS)
        );
        return scLocation;
    }

    public Future<Void> upsertRemote(SCLocation scLocation, String private_code) {
        return Executors.newSingleThreadExecutor().submit(() -> {
            api.putSCLocation(scLocation, private_code);
            return null;
        });
    }

    public void updateSCLocationLive(LiveData<SCLocation> scLocation, String private_code){
        var executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            SCLocation location = scLocation.getValue();
            if (location != null) {
                api.patchSCLocation(location,private_code,false);
            }
        }, 0,LIVE_UPDATE_TIME, TimeUnit.MILLISECONDS);
    }

    public void killAllRemoteLiveThreads(){
        for(var poller : remoteUpdateThreads){
            poller.cancel(true);
        }
        remoteUpdateThreads.clear();
    }


}
