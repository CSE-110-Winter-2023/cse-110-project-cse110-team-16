package com.example.cse110_team16_project.classes.ViewModels;
import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.cse110_team16_project.Database.SCLocationDatabase;
import com.example.cse110_team16_project.Database.SCLocationRepository;
import com.example.cse110_team16_project.classes.CoordinateClasses.SCLocation;
import com.example.cse110_team16_project.classes.Misc.Utilities;

import java.util.List;

public class ListViewModel extends AndroidViewModel {
    private LiveData<List<SCLocation>> locations;
    private final SCLocationRepository repo;

    public ListViewModel(@NonNull Application application) {
        super(application);
        var context = application.getApplicationContext();
        var db = SCLocationDatabase.provide(context);
        var dao = db.getDao();
        this.repo = new SCLocationRepository(dao);
    }

    /**
     * Load all locations from the database.
     * @return a LiveData object that will be updated when any locations change.
     */
    public LiveData<List<SCLocation>> getSCLocations() {
        if (locations == null) {
            locations = repo.getAllLocalLive();
        }
        return locations;
    }

    /**
     * Open a location in the database. If the location does not exist, create it.
     * @param public_code the public_code of the location
     * @return a LiveData object that will be updated when this location changes.
     */
    public SCLocation getOrCreateSCLocation(String public_code, Activity activity) {
        if (!repo.existsLocal(public_code)) {
            if(!repo.existsRemote(public_code)){
                Utilities.showError(activity, "UID does not exist.");
                return null;
            }
            SCLocation newLocation = repo.getRemote(public_code);
            if(newLocation != null) {
                repo.upsertLocal(newLocation);
                return newLocation;
            }
            else {
                Utilities.showError(activity, "Unable to retrieve location " +
                        "from remote database.");
                return null;
            }
        }
        else Utilities.showAlert(activity, "Location already exists.");
        return repo.getLocal(public_code);
    }

    public void delete(SCLocation location) {
        repo.deleteLocal(location);
    }
}
