package com.example.cse110_team16_project.classes;

import android.location.Location;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cse110_team16_project.R;
import com.example.cse110_team16_project.classes.Misc.Converters;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GPSStatus {
    private LiveData<Location> location;
    private View statusDot;
    private TextView gpsText;
    private long initTime;

    private ScheduledFuture<?> future;

    private static final int TIME_THRESHOLD = 60000;
    private static final int ONE_MIN = 60000;
    private static final int ONE_HOUR = 3600000;
    public static final int REFRESH_PERIOD = 6000;

    public GPSStatus(LiveData<Location> loc, View v, TextView gt) {
        this.location = loc;
        this.statusDot = v;
        this.gpsText = gt;
        this.initTime = -REFRESH_PERIOD;
    }

    public long getLocationAge(long currentMillis) {
        Location currLoc = this.location.getValue();
        if (currLoc != null) {
            long timeElapsed = currentMillis - currLoc.getTime();
            Log.d("Time Elapsed", timeElapsed + "");
            return timeElapsed;
        }
        else {
            Log.d("GPS", "No data yet!");
            //long initTimeNow = initTime;
            Log.d("GPS", "initTime = " + initTime);
            return this.initTime;
        }
    }

    public boolean isLocationLive(long currentMillis) {
        Long locationAge = getLocationAge(currentMillis);
        return locationAge < TIME_THRESHOLD;
    }

    public void updateGPSLostTime() {
        long locationAge = getLocationAge(System.currentTimeMillis());
        if (locationAge < ONE_HOUR) {
            this.gpsText.setText(Converters.milisecToMins(locationAge) + "m");
        } else {
            this.gpsText.setText(Converters.milisecToHours(locationAge) + "h");
        }
    }

    public void trackGPSStatus() {
        var executor = Executors.newSingleThreadScheduledExecutor();
        future = executor.scheduleAtFixedRate(() -> {
           updateGPSStatus();
        }, 0, REFRESH_PERIOD, TimeUnit.MILLISECONDS);
    }

    public void stopTracking() {
        if (this.future != null && !this.future.isCancelled()) {
            future.cancel(true);
        }
    }

    public void updateGPSStatus(){
        try {
            this.initTime += REFRESH_PERIOD;
            if (isLocationLive(System.currentTimeMillis()) && this.location.getValue() != null) {
                this.setGreen();
                this.gpsText.setText("");
                Log.d("GPS", "GPS status set to green");
            } else {
                this.setRed();
                updateGPSLostTime();
                Log.d("GPS", "GPS status set to red");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setGreen() {
        this.statusDot.setBackgroundResource(R.drawable.gps_green);
        this.statusDot.setTag(R.drawable.gps_green);
    }

    private void setRed() {
        this.statusDot.setBackgroundResource(R.drawable.gps_red);
        this.statusDot.setTag(R.drawable.gps_red);
    }

    @VisibleForTesting
    public void setMockLocation(Location location){
        MutableLiveData<Location> newloc = new MutableLiveData<>();
        newloc.setValue(location);
        this.location = newloc;
    }
}
