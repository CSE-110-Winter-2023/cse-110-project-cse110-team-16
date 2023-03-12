package com.example.cse110_team16_project;

import static org.junit.Assert.assertEquals;

import android.location.Location;
import android.widget.TextView;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;
import androidx.test.rule.GrantPermissionRule;

import com.example.cse110_team16_project.classes.GPSStatus;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class Story7ScenarioTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule
            .grant(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Test
    public void story7Scenario1() {
        // on when enter app
        // turn off gps
        // green --> red --> green
        ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onActivity(activity -> {
            TextView gpsLight = activity.findViewById(R.id.gpsLight);

            MutableLiveData<Location> mockLiveLoc = new MutableLiveData<>();
            Location mockLoc = new Location("dummy provider");
            mockLoc.setLatitude(100);
            mockLoc.setLongitude(-100);
            mockLiveLoc.setValue(mockLoc);
            GPSStatus mockGPSStatus = new GPSStatus(mockLiveLoc, activity.findViewById(R.id.gpsLight),
                    activity.findViewById(R.id.gpsText));
            activity.setGpsStatus(mockGPSStatus);
            mockGPSStatus.updateGPSStatus();
            assertEquals(R.drawable.gps_green, gpsLight.getTag());
            mockGPSStatus.setMockLocation(null);
            mockGPSStatus.updateGPSStatus();
            assertEquals(R.drawable.gps_red, gpsLight.getTag());
            mockGPSStatus.setMockLocation(mockLoc);
            mockGPSStatus.updateGPSStatus();
            assertEquals(R.drawable.gps_green, gpsLight.getTag());
        });
    }

    @Test
    public void story7Scenario2() {
        // on when enter app
        // turn off gps
        // red --> green
        ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onActivity(activity -> {
            TextView gpsLight = activity.findViewById(R.id.gpsLight);

            MutableLiveData<Location> mockLiveLoc = new MutableLiveData<>();
            Location mockLoc = new Location("dummy provider");
            mockLoc.setLatitude(100);
            mockLoc.setLongitude(-100);
            mockLiveLoc.setValue(null);
            GPSStatus mockGPSStatus = new GPSStatus(mockLiveLoc, activity.findViewById(R.id.gpsLight),
                    activity.findViewById(R.id.gpsText));
            activity.setGpsStatus(mockGPSStatus);
            mockGPSStatus.updateGPSStatus();
            assertEquals(R.drawable.gps_red, gpsLight.getTag());
            mockGPSStatus.setMockLocation(mockLoc);
            mockGPSStatus.updateGPSStatus();
            assertEquals(R.drawable.gps_green, gpsLight.getTag());
        });
    }
}
