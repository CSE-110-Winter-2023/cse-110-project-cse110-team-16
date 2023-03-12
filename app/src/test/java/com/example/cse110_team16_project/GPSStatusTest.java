package com.example.cse110_team16_project;

import static org.junit.Assert.assertEquals;

import android.location.Location;
import android.os.SystemClock;
import androidx.lifecycle.MutableLiveData;

import com.example.cse110_team16_project.classes.GPSStatus;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class GPSStatusTest {
    private static final int REFRESH_PERIOD = 15 * 1000;
    @Test
    public void testGetLocationAge() {
        MutableLiveData<Location> mockLiveLoc = new MutableLiveData<>();
        Location mockLoc  = new Location("dummy provider");
        mockLoc.setLatitude(-100);
        mockLoc.setLongitude(100);
        long currTime = System.currentTimeMillis();
        mockLoc.setTime(currTime);
        mockLiveLoc.setValue(mockLoc);

        // GPS enabled, we have location
        GPSStatus gpsStatus = new GPSStatus(mockLiveLoc, null, null);
        long locAge = gpsStatus.getLocationAge();
        assertEquals(100, locAge);

        // GPS initially disabled, location null
        mockLiveLoc.setValue(null);
        gpsStatus = new GPSStatus(mockLiveLoc, null, null);
        locAge = gpsStatus.getLocationAge();
        assertEquals(-REFRESH_PERIOD, locAge);
    }

    @Test
    public void testIsLocationLive() {
        MutableLiveData<Location> mockLiveLoc = new MutableLiveData<>();
        Location mockLoc  = new Location("dummy provider");
        mockLoc.setLatitude(-100);
        mockLoc.setLongitude(100);
        long currTime = System.currentTimeMillis();
        mockLoc.setTime(currTime);
        mockLiveLoc.setValue(mockLoc);
        GPSStatus gpsStatus = new GPSStatus(mockLiveLoc, null, null);
        assertEquals(true, gpsStatus.isLocationLive());

        long timeNow = System.currentTimeMillis();
        SystemClock.setCurrentTimeMillis(timeNow + 60000);
        assertEquals(false, gpsStatus.isLocationLive());
    }
}
