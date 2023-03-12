package com.example.cse110_team16_project;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.location.Location;
import android.widget.TextView;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.rule.GrantPermissionRule;

import com.example.cse110_team16_project.Database.SCLocationDao;
import com.example.cse110_team16_project.Database.SCLocationDatabase;
import com.example.cse110_team16_project.classes.GPSStatus;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowSystemClock;

import java.util.concurrent.TimeUnit;

@RunWith(RobolectricTestRunner.class)
public class Story8ScenarioTest {
    private SCLocationDao dao;
    private SCLocationDatabase db;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule
            .grant(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Before
    public void createDb(){
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, SCLocationDatabase.class)
                .allowMainThreadQueries()
                .build();
        dao = db.getDao();
        SCLocationDatabase.inject(db);
    }

    @After
    public void closeDb() throws Exception {
        db.close();
    }

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
            MutableLiveData<Location> mockLiveLoc = new MutableLiveData<>();
            Location mockLoc = new Location("dummy provider");
            long timeNow = System.currentTimeMillis();
            mockLoc.setTime(timeNow);
            mockLoc.setLatitude(100);
            mockLoc.setLongitude(-100);
            mockLiveLoc.setValue(mockLoc);
            GPSStatus mockGPSStatus = new GPSStatus(mockLiveLoc, activity.findViewById(R.id.gpsLight),
                    activity.findViewById(R.id.gpsText));
            mockGPSStatus.updateGPSStatus();
            // now turn gps off for 1 min
            ShadowSystemClock.advanceBy(60, TimeUnit.SECONDS);
            mockGPSStatus.updateGPSStatus();
            TextView gpsText = activity.findViewById(R.id.gpsText);
            assertEquals("1m", gpsText.getText().toString());

            // turn gps off for another min
            ShadowSystemClock.advanceBy(60, TimeUnit.SECONDS);
            mockGPSStatus.updateGPSStatus();
            assertEquals("2m", gpsText.getText().toString());
            // turn gps back on for 30s
            mockLoc.setElapsedRealtimeNanos(1000000L * 120 * 1000);
            mockGPSStatus.setMockLocation(mockLoc);
            ShadowSystemClock.advanceBy(30, TimeUnit.SECONDS);
            mockGPSStatus.updateGPSStatus();
            assertEquals("", gpsText.getText().toString());
        });
    }

    @Test
    public void story8Scenario2() {
        // turn on gps
        // turn off gps for 59m
        // turn off gps for another 1m
        ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onActivity(activity -> {
            MutableLiveData<Location> mockLiveLoc = new MutableLiveData<>();
            Location mockLoc = new Location("dummy provider");
            long timeNow = System.currentTimeMillis();
            mockLoc.setTime(timeNow);
            mockLoc.setLatitude(100);
            mockLoc.setLongitude(-100);
            mockLiveLoc.setValue(mockLoc);
            GPSStatus mockGPSStatus = new GPSStatus(mockLiveLoc, activity.findViewById(R.id.gpsLight),
                    activity.findViewById(R.id.gpsText));
            mockGPSStatus.updateGPSStatus();
            // now turn gps off for 1 min
            ShadowSystemClock.advanceBy(59, TimeUnit.MINUTES);
            mockGPSStatus.updateGPSStatus();
            TextView gpsText = activity.findViewById(R.id.gpsText);
            assertEquals("59m", gpsText.getText().toString());

            ShadowSystemClock.advanceBy(1,TimeUnit.MINUTES);
            mockGPSStatus.updateGPSStatus();
            assertEquals("1h", gpsText.getText().toString());
        });
    }
}