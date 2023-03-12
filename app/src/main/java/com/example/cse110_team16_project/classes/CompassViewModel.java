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
    public static final double SECTOR_RADIUS = 45;
    public static final int[] MILES_DISTANCES = new int[]{0, 1, 10, 500}; //first index is zero for calculations
    private List<LiveData<SCLocation>> scLocations;
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
    public List<LiveData<SCLocation>> getSCLocations() {
        if(scLocations == null) return refreshSCLocations();
        return scLocations;
    }

    public List<LiveData<SCLocation>> refreshSCLocations(){
            scLocations = new ArrayList<>();
            List<String> public_codes = repo.getLocalPublicCodes();
            for(String code: public_codes) {
                scLocations.add(repo.getRemoteLive(code));
            }
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
            for(int i = 1; i <= numZones; i++){
                if (mile.getMiles() < MILES_DISTANCES[i]){
                    screenDistances.add(((mile.getMiles()/(MILES_DISTANCES[i]-MILES_DISTANCES[i-1])) + i-1)*SECTOR_RADIUS);
                    break;
                }
            }
        }
        return screenDistances;
    }
}

