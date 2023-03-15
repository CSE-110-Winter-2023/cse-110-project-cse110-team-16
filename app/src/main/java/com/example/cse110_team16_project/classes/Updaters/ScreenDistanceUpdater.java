package com.example.cse110_team16_project.classes.Updaters;

import android.app.Activity;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cse110_team16_project.classes.Misc.Converters;
import com.example.cse110_team16_project.classes.Units.Meters;
import com.example.cse110_team16_project.classes.Units.Miles;

import java.util.ArrayList;
import java.util.List;

public class ScreenDistanceUpdater {
    public static final double LARGEST_RADIUS = 500; //set to whatever proper value
    public static final int[] MILES_DISTANCES = new int[]{0, 1, 10, 500}; //first index is zero for calculations

    private final MutableLiveData<List<Double>> screenDistances = new MutableLiveData<>();
    private final Activity activity;
    public ScreenDistanceUpdater(Activity activity) {
        this.activity = activity;
    }

    public void startObserve(LiveData<List<Meters>> distances) {
        distances.observe((LifecycleOwner) activity, (obvDistances) -> {
            screenDistances.postValue(findScreenDistance(obvDistances,3));
        });
    }
    public List<Double> findScreenDistance(List<Meters> meters, int numZones) {
        List<Miles> miles = Converters.listMetersToMiles(meters);
        if(miles == null) return null;
        List<Double> screenDistances = new ArrayList<>();
        for(Miles mile: miles){
            if(mile == null) {
                screenDistances.add(null);
                continue;
            }
            if(mile.getMiles() > MILES_DISTANCES[numZones]) {
                screenDistances.add(LARGEST_RADIUS);
                continue;
            }
            for(int i = 1; i <= numZones; i++){
                if (mile.getMiles() < MILES_DISTANCES[i]){
                    screenDistances.add(((mile.getMiles()/(MILES_DISTANCES[i]-MILES_DISTANCES[i-1])) + i-1)* LARGEST_RADIUS/numZones);
                    break;
                }
            }
        }
        return screenDistances;
    }

    public LiveData<List<Double>> getScreenDistances() {
        return this.screenDistances;
    }
}
