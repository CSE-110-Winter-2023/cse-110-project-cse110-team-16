package com.example.cse110_team16_project.classes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cse110_team16_project.Database.SCLocationDatabase;
import com.example.cse110_team16_project.Database.SCLocationRepository;
import com.example.cse110_team16_project.Units.Meters;
import com.example.cse110_team16_project.Units.Miles;

import java.util.ArrayList;
import java.util.List;

public class CompassViewModel extends AndroidViewModel {
    private static final double SECTOR_RADIUS = 45;
    private static int[] MILES_DISTANCES = new int[]{1, 10, 500};
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
        if(scLocations == null) return refreshSCLocations();
        return scLocations;
    }

    public LiveData<List<SCLocation>> refreshSCLocations(){
            scLocations = new MutableLiveData<>();
            List<String> public_codes = repo.getLocalPublicCodes();
            scLocations = repo.getRemote(public_codes);
        return scLocations;
    }

    public List<Double> findScreenDistance(List<Meters> meters, int numZones) {
        List<Miles> miles = Converters.listMetersToMiles(meters);
        if(miles == null) return null;
        List<Double> screenDistances = new ArrayList<>();
        for(Miles mile: miles){
            if(mile.getMiles() > MILES_DISTANCES[numZones]) {
                screenDistances.add(numZones*SECTOR_RADIUS);
                continue;
            }
            for(int i = 0; i < numZones; i++){
                if (mile.getMiles() < MILES_DISTANCES[i]){
                    screenDistances.add(((mile.getMiles()/MILES_DISTANCES[i]) + i)*SECTOR_RADIUS);
                    break;
                }
            }
        }
        return screenDistances;
    }
}

