package com.example.cse110_team16_project;

import static org.junit.Assert.assertEquals;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.cse110_team16_project.Units.Degrees;
import com.example.cse110_team16_project.classes.OrientationService;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class OrientationServiceTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void testMockingOrientationService() {

        var scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(activity -> {
            var orientationService = OrientationService.singleton(activity);

            var mockOrientation = new Degrees(180);
            orientationService.setMockOrientationSource(mockOrientation);
            // We don't want to have to do this! It's not our job to tell the activity!

            assertEquals(orientationService.getOrientation().getValue().getRadians(),Math.PI,0.1);
        });
    }
}
