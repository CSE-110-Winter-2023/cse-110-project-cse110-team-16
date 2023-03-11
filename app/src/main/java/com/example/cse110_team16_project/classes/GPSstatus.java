package com.example.cse110_team16_project.classes;

import android.location.Location;
import android.os.Build;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.LiveData;

import com.example.cse110_team16_project.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GPSstatus{
    private LiveData<Location> location;

    private View view;

    public GPSstatus(LiveData<Location> loc, View v){
        this.location = loc;
        this.view = v;
    }

    private long getLocationAge(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return Objects.requireNonNull(this.location.getValue()).getElapsedRealtimeAgeMillis();
        }
        return 0;
    }

    public boolean isLocationLive(){
        Log.d("GPS", Long.toString(getLocationAge()));
       return getLocationAge() < 1000;
    }

    public void trackGPSStatus(){
        var executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            try{
                if (isLocationLive()){
                    this.setGreen();
                    Log.d("GPS", "GPS status set to green");
                } else {
                    this.setRed();
//              Log.d("GPS", "GPS status set to red");
                }
            } catch (Exception e){
                e.printStackTrace();
            }

        },0,3, TimeUnit.SECONDS);
    }

    private void setGreen(){
        this.view.setBackgroundResource(R.drawable.gps_green);
    }

    private void setRed(){
        this.view.setBackgroundResource(R.drawable.gps_red);
    }
}
