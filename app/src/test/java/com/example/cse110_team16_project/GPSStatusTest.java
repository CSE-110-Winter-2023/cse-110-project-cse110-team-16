package com.example.cse110_team16_project;

import static org.junit.Assert.assertEquals;

import android.location.Location;
import android.os.SystemClock;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cse110_team16_project.classes.GPSstatus;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowSystemClock;

import javax.xml.datatype.Duration;

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
        GPSstatus gpsStatus = new GPSstatus(mockLiveLoc, null, null);
        long locAge = gpsStatus.getLocationAge();
        assertEquals(100, locAge);

        // GPS initially disabled, location null
        mockLiveLoc.setValue(null);
        gpsStatus = new GPSstatus(mockLiveLoc, null, null);
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
        GPSstatus gpsStatus = new GPSstatus(mockLiveLoc, null, null);

        // Location is live
        assertEquals(true, gpsStatus.isLocationLive());

        // Location is old, not live
        try {
            long timeNow = System.currentTimeMillis();
            SystemClock.setCurrentTimeMillis(timeNow + 60000);
            assertEquals(false, gpsStatus.isLocationLive());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
