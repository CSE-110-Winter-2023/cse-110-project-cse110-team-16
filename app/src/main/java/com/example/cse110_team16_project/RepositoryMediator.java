package com.example.cse110_team16_project;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.cse110_team16_project.Database.SCLocationDatabase;
import com.example.cse110_team16_project.Database.SCLocationRepository;
import com.example.cse110_team16_project.classes.Units.Meters;
import com.example.cse110_team16_project.classes.Units.Miles;
import com.example.cse110_team16_project.classes.Misc.Converters;
import com.example.cse110_team16_project.classes.CoordinateClasses.SCLocation;

import java.util.ArrayList;
import java.util.List;

public class RepositoryMediator {

    private List<LiveData<SCLocation>> scLocations;
    private final SCLocationRepository repo;

    public RepositoryMediator(SCLocationRepository repo) {
        this.repo = repo;
    }

    /**
     * Load all user stored SCLocations from the remote database.
     * @return a LiveData object that will be updated when any location changes.
     */
    public List<LiveData<SCLocation>> getSCLocations(List<String> public_codes) {
        if(scLocations == null) return refreshSCLocations(public_codes);
        return scLocations;
    }

    public List<LiveData<SCLocation>> refreshSCLocations(List<String> public_codes){
        repo.killAllRemoteLiveThreads();
        scLocations = new ArrayList<>();
            for(String code: public_codes) {
                scLocations.add(repo.getRemoteLive(code));
            }
        return scLocations;
    }

}

