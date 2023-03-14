package com.example.cse110_team16_project;

import static com.example.cse110_team16_project.classes.GPSStatus.REFRESH_PERIOD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.location.Location;
import android.os.SystemClock;
import androidx.lifecycle.MutableLiveData;

import com.example.cse110_team16_project.classes.GPSStatus;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowSystem;
import org.robolectric.shadows.ShadowSystemClock;

import java.time.Duration;

@RunWith(RobolectricTestRunner.class)
public class GPSStatusTest {

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
        long locAge = gpsStatus.getLocationAge(currTime + 100);
        assertEquals(100, locAge);

        // GPS initially disabled, location null
        mockLiveLoc.setValue(null);
        gpsStatus = new GPSStatus(mockLiveLoc, null, null);
        locAge = gpsStatus.getLocationAge(System.currentTimeMillis());
        assertEquals(-REFRESH_PERIOD, locAge);
    }

    @Test
    public void testIsLocationLive() {
        MutableLiveData<Location> mockLiveLoc = new MutableLiveData<>();
        Location mockLoc  = new Location("dummy provider");
        mockLoc.setLatitude(-100);
        mockLoc.setLongitude(100);
        long currTime = ShadowSystem.currentTimeMillis();
        mockLoc.setTime(currTime);
        mockLiveLoc.setValue(mockLoc);
        GPSStatus gpsStatus = new GPSStatus(mockLiveLoc, null, null);
        assertEquals(true, gpsStatus.isLocationLive(ShadowSystem.currentTimeMillis()));

        long timeNow = ShadowSystem.currentTimeMillis();
        ShadowSystemClock.advanceBy(Duration.ofMinutes(1));
        assertTrue(ShadowSystem.currentTimeMillis()-timeNow >= 60000);
        try {
            Thread.sleep(REFRESH_PERIOD + 50);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertEquals(false, gpsStatus.isLocationLive(ShadowSystem.currentTimeMillis()));
    }
}
