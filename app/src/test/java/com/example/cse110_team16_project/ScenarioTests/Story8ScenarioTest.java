package com.example.cse110_team16_project.ScenarioTests;

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

import com.example.cse110_team16_project.CompassActivity;
import com.example.cse110_team16_project.Database.SCLocationDao;
import com.example.cse110_team16_project.Database.SCLocationDatabase;
import com.example.cse110_team16_project.R;
import com.example.cse110_team16_project.classes.DeviceInfo.GPSStatus;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

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
            long currentTime = 10000000;
            mockLoc.setTime(currentTime);
            mockLoc.setLatitude(100);
            mockLoc.setLongitude(-100);
            GPSStatus mockGPSStatus = new GPSStatus(mockLiveLoc, activity.findViewById(R.id.gpsLight),
                    activity.findViewById(R.id.gpsText));
            mockLiveLoc.setValue(mockLoc);
            currentTime += 60000;
            mockGPSStatus.updateGPSStatus(currentTime);
            TextView gpsText = activity.findViewById(R.id.gpsText);
            assertEquals("1m", gpsText.getText().toString());

            // turn gps off for another min
            currentTime += 60000;
            mockGPSStatus.updateGPSStatus(currentTime);
            assertEquals("2m", gpsText.getText().toString());
            // turn gps back on for 30s
            mockLoc.setTime(currentTime);
            mockGPSStatus.setMockLocation(mockLoc);
            currentTime += 30000;
            mockGPSStatus.updateGPSStatus(currentTime);
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
            long currentTime = 10000000;
            mockLoc.setTime(currentTime);
            mockLoc.setLatitude(100);
            mockLoc.setLongitude(-100);
            mockLiveLoc.setValue(mockLoc);
            GPSStatus mockGPSStatus = new GPSStatus(mockLiveLoc, activity.findViewById(R.id.gpsLight),
                    activity.findViewById(R.id.gpsText));
            currentTime+=59*60000;
            mockGPSStatus.updateGPSStatus(currentTime);
            TextView gpsText = activity.findViewById(R.id.gpsText);
            assertEquals("59m", gpsText.getText().toString());

            currentTime+=60000;
            mockGPSStatus.updateGPSStatus(currentTime);
            assertEquals("1h", gpsText.getText().toString());
        });
    }
}
