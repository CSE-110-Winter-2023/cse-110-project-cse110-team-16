package com.example.cse110_team16_project;

import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.annotation.LooperMode.Mode.PAUSED;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.GrantPermissionRule;

import com.example.cse110_team16_project.classes.CompassUIManager;
import com.example.cse110_team16_project.classes.Coordinates;
import com.example.cse110_team16_project.classes.Degrees;
import com.example.cse110_team16_project.classes.Home;
import com.example.cse110_team16_project.classes.HomeDirectionUpdater;
import com.example.cse110_team16_project.classes.User;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.LooperMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@RunWith(RobolectricTestRunner.class)
@LooperMode(PAUSED)
public class CompassUIManagerTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule
            .grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    //not much mocking and very intertwined for a unit test
    @Test
    public void testUpdateCompassDirection() {
        ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(CompassActivity.class);

        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onActivity(activity -> {
            activity.getUser().setDirection(new Degrees(30));
            assertEquals(30,activity.getUser().getDirection().getValue().getDegrees(),0.5);
            try {
                activity.getManager().getFuture().get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            shadowOf(Looper.getMainLooper()).idle();
            assertEquals(-30f,activity.findViewById(R.id.compassRing).getRotation(),0.1f);
        });
    }
}
