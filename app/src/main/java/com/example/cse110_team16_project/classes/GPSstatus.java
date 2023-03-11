package com.example.cse110_team16_project.classes;

import android.location.Location;
import android.os.Build;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GPSstatus {
    private LiveData<Location> location;

    private View view;

    public GPSstatus(LiveData<Location> loc, View v){
        this.location = loc;
        this.view = v;
    }

    private long getLocationAge(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return this.location.getValue().getElapsedRealtimeAgeMillis();
        }
    }

    public boolean isLocationLive(){
       return getLocationAge() < 200;
    }

    public void trackGPSStatus(){
        var executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            if (isLocationLive()){
                this.setGreen();
                Log.d("GPS", "GPS status set to green");
            } else {
                this.setRed();
                Log.d("GPS", "GPS status set to red");
            }
        },0,10, TimeUnit.SECONDS);
    }

    public void setGreen(){
        this.view.setBackgroundColor(0x00FF00);
    }

    public void setRed(){
        this.view.setBackgroundColor(0xFF0000);
    }
}
