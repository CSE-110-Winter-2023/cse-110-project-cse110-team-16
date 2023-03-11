package com.example.cse110_team16_project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.rule.GrantPermissionRule;

import com.example.cse110_team16_project.Units.Degrees;
import com.example.cse110_team16_project.classes.Misc.Converters;
import com.example.cse110_team16_project.classes.CoordinateClasses.Coordinates;
import com.example.cse110_team16_project.classes.DeviceTracker;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class DeviceTrackerTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule
            .grant(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Test
    public void testMockingDeviceTracker() {

        var scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(activity -> {
            var deviceTracker = new DeviceTracker(activity);
            deviceTracker.mockUserDirection(new Degrees(350));
            deviceTracker.mockUserLocation(Converters.CoordinatesToLocation(new Coordinates(6,9)));

            assertEquals(deviceTracker.getOrientation().getValue().getRadians(),(350.0/360)*2*Math.PI,0.1);
            assertEquals(deviceTracker.getCoordinates().getValue().getLatitude(),6,0.1);
            assertEquals(deviceTracker.getCoordinates().getValue().getLongitude(),9,0.1);
        });
    }
}
