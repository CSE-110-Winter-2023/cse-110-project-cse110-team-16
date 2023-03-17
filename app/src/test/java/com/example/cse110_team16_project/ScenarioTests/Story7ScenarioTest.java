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
public class Story7ScenarioTest {
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
            long currentTime = 100000000;
            mockLoc.setTime(currentTime);
            mockLiveLoc.setValue(mockLoc);
            GPSStatus mockGPSStatus = new GPSStatus(mockLiveLoc, activity.findViewById(R.id.gpsLight),
                    activity.findViewById(R.id.gpsText));
            mockGPSStatus.updateGPSStatus(currentTime+=3000);
            assertEquals(R.drawable.gps_green, gpsLight.getTag());
            mockGPSStatus.setMockLocation(null);
            mockGPSStatus.updateGPSStatus(currentTime+=3000);
            assertEquals(R.drawable.gps_red, gpsLight.getTag());
            mockGPSStatus.setMockLocation(mockLoc);
            mockGPSStatus.updateGPSStatus(currentTime+=3000);
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
            int currentTime = 100000000;
            mockGPSStatus.updateGPSStatus(currentTime);
            assertEquals(R.drawable.gps_red, gpsLight.getTag());
            mockLoc.setTime(currentTime);
            mockGPSStatus.setMockLocation(mockLoc);
            mockGPSStatus.updateGPSStatus(currentTime+=3000);
            assertEquals(R.drawable.gps_green, gpsLight.getTag());
        });
    }
}
