package com.example.cse110_team16_project;

import static org.junit.Assert.assertEquals;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;

import com.example.cse110_team16_project.classes.Misc.Converters;
import com.example.cse110_team16_project.classes.CoordinateClasses.Coordinates;
import com.example.cse110_team16_project.classes.LocationService;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import androidx.test.rule.GrantPermissionRule;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class LocationServiceTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule
            .grant(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Test
    public void testMockingLocationService() {

        var scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(activity -> {
            var locationService = LocationService.singleton(activity,200,20);

            var mockOrientation = Converters.CoordinatesToLocation(new Coordinates(3,3));
            locationService.setMockLocationSource(mockOrientation);

            assertEquals(locationService.getLocation().getValue().getLatitude(),3,0.1);
            assertEquals(locationService.getLocation().getValue().getLongitude(),3,0.1);
        });
    }
}
