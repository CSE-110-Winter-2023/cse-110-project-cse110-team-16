package com.example.cse110_team16_project.classes;

import android.location.Location;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.LiveData;

import com.example.cse110_team16_project.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GPSstatus {
    private LiveData<Location> location;
    private View statusDot;
    private TextView gpsText;
    private long initTime;

    private static final int TIME_THRESHOLD = 60000;
    private static final int ONE_MIN = 60000;
    private static final int ONE_HOUR = 3600000;

    public GPSstatus(LiveData<Location> loc, View v, TextView gt) {
        this.location = loc;
        this.statusDot = v;
        this.gpsText = gt;
        this.initTime = 0;
    }

    private long getLocationAge() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Location currLoc = this.location.getValue();
            if (currLoc != null) {
                return currLoc.getElapsedRealtimeAgeMillis();
            }
            else {
                Log.d("GPS", "No data yet!");
                long initTimeNow = initTime;
                initTime += ONE_MIN / 60;
                return initTimeNow;
            }
        }
        return 0;
    }

    public boolean isLocationLive() {
        Log.d("GPS", Long.toString(getLocationAge()));
        return getLocationAge() < TIME_THRESHOLD;
    }

    public void updateGPSLostTime() {
        long locationAge = getLocationAge();
        if (locationAge < ONE_HOUR) {
            this.gpsText.setText(Converters.milisecToMins(locationAge) + "m");
        } else {
            this.gpsText.setText(Converters.milisecToHours(locationAge) + "h");
        }
    }

    public void trackGPSStatus() {
        var executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            try {
                if (isLocationLive() && this.location.getValue() != null) {
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
        }, 0, 3, TimeUnit.SECONDS);
    }

    private void setGreen() {
        this.statusDot.setBackgroundResource(R.drawable.gps_green);
    }

    private void setRed() {
        this.statusDot.setBackgroundResource(R.drawable.gps_red);
    }
}
