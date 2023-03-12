package com.example.cse110_team16_project;

import android.location.Location;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cse110_team16_project.classes.GPSstatus;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class GPSStatusTest {
    @Test
    public void testGetLocationAge() {
        MutableLiveData<Location> mockLiveLoc = new MutableLiveData<>();
        long currTime = System.currentTimeMillis();
        Location mockLoc  = new Location("dummy provider");
        mockLoc.setLatitude(-100);
        mockLoc.setLongitude(100);
        mockLoc.setTime(currTime);
        mockLiveLoc.postValue(mockLoc);
        GPSstatus gpsStatus = new GPSstatus(mockLiveLoc, null, null);
        long locAge = gpsStatus.getLocationAge();
    }
}
