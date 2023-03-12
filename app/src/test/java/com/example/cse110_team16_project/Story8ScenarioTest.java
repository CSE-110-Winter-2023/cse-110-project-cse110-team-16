package com.example.cse110_team16_project;

import static org.junit.Assert.assertEquals;

import android.location.Location;
import android.os.SystemClock;
import android.widget.TextView;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;
import androidx.test.rule.GrantPermissionRule;

import com.example.cse110_team16_project.classes.GPSstatus;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.w3c.dom.Text;

import java.io.SyncFailedException;

@RunWith(RobolectricTestRunner.class)
public class Story8ScenarioTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule
            .grant(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Test
    public void story8Scenario1() {
        // gps on when enter compassactivity
        // turn off gps for >1min
        // "1m" should appear on top of the screen
        // turn off gps for 1 min more
        // "2m" should appear on top of the screen
        // Turn gps on for >30s
        // "2m" should disappear
        ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onActivity(activity -> {
            TextView gpsLight = activity.findViewById(R.id.gpsLight);

            MutableLiveData<Location> mockLiveLoc = new MutableLiveData<>();
            Location mockLoc = new Location("dummy provider");
            long timeNow = System.currentTimeMillis();
            mockLoc.setTime(timeNow);
            mockLoc.setLatitude(100);
            mockLoc.setLongitude(-100);
            mockLiveLoc.setValue(mockLoc);
            GPSstatus mockGPSstatus = new GPSstatus(mockLiveLoc, activity.findViewById(R.id.gpsLight),
                    activity.findViewById(R.id.gpsText));
            System.out.println(mockLiveLoc.getValue().getElapsedRealtimeAgeMillis());

            activity.setGpsstatus(mockGPSstatus);
            mockGPSstatus.updateGPSStatus();
            // now turn gps off for 1 min
            SystemClock.setCurrentTimeMillis(60000);
            mockGPSstatus.updateGPSStatus();
            TextView gpsText = activity.findViewById(R.id.gpsText);
            assertEquals("1m", gpsText.getText().toString());

            // turn gps off for another min
            SystemClock.setCurrentTimeMillis(120000);
            mockGPSstatus.updateGPSStatus();
            assertEquals("2m", gpsText.getText().toString());
            // turn gps back on for 30s
            mockLoc.setTime(30000);
            //System.out.println("mockLoc time in ms: " + mockLoc.getTime());
            mockGPSstatus.setMockLocation(mockLoc);
            SystemClock.setCurrentTimeMillis(30000);
            mockGPSstatus.updateGPSStatus();
            assertEquals("", gpsText.getText().toString());
        });
    }

    @Test
    public void story8Scenario2() {
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
            GPSstatus mockGPSstatus = new GPSstatus(mockLiveLoc, activity.findViewById(R.id.gpsLight),
                    activity.findViewById(R.id.gpsText));
            activity.setGpsstatus(mockGPSstatus);
            mockGPSstatus.updateGPSStatus();
            assertEquals(R.drawable.gps_red, gpsLight.getTag());
            mockGPSstatus.setMockLocation(mockLoc);
            mockGPSstatus.updateGPSStatus();
            assertEquals(R.drawable.gps_green, gpsLight.getTag());
        });
    }
}
