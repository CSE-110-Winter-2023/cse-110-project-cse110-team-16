package com.example.cse110_team16_project.classes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cse110_team16_project.Database.SCLocationDatabase;
import com.example.cse110_team16_project.Database.SCLocationRepository;

import java.util.List;

public class CompassViewModel extends AndroidViewModel {
    private LiveData<List<SCLocation>> scLocations;
    private final SCLocationRepository repo;

    public CompassViewModel(@NonNull Application application) {
        super(application);
        var context = application.getApplicationContext();
        var db = SCLocationDatabase.provide(context);
        var dao = db.getDao();
        this.repo = new SCLocationRepository(dao);
    }

    /**
     * Load all user stored SCLocations from the remote database.
     * @return a LiveData object that will be updated when any location changes.
     */
    public LiveData<List<SCLocation>> getSCLocations() {
        if (scLocations == null) {
            scLocations = new MutableLiveData<>();
            List<String> public_codes = repo.getLocalPublicCodes();
            scLocations = repo.getRemote(public_codes);
        }
        return scLocations;
    }



}

