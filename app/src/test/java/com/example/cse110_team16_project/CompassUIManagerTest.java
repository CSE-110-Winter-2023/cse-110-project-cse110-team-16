package com.example.cse110_team16_project;

import static org.junit.Assert.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.GrantPermissionRule;

import com.example.cse110_team16_project.classes.CompassUIManager;
import com.example.cse110_team16_project.classes.Coordinates;
import com.example.cse110_team16_project.classes.Home;
import com.example.cse110_team16_project.classes.HomeDirectionUpdater;
import com.example.cse110_team16_project.classes.User;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class CompassUIManagerTest {

    @Rule
    public ActivityScenarioRule<CompassActivity> rule = new ActivityScenarioRule<>(CompassActivity.class);
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule
            .grant(android.Manifest.permission.ACCESS_FINE_LOCATION);
    @Test
    public void testupdateCompassDirection() {
        ActivityScenario<CompassActivity> scenario = rule.getScenario();

        Home home = new Home(new Coordinates(42.68753,38.6543),
                "Parents' Home");
        List<Home> homes = new ArrayList<>();
        homes.add(home);
        User user = new User();
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.onActivity(activity ->
        {
        HomeDirectionUpdater homeDirectionUpdater =
                new HomeDirectionUpdater(activity, homes, user);
            CompassUIManager compassUIManager = new CompassUIManager(activity,user,
                    homeDirectionUpdater, activity.findViewById(R.id.compassRing),
                    activity.findViewById(R.id.sampleHome));
            user.setDirection(30);
            user.setCoordinates(new Coordinates(41.1432,37.5342));
                compassUIManager.updateCompassDirection(user.getDirection().getValue());
            assertEquals(-30f,activity.findViewById(R.id.compassRing).getRotation(),0.1f);
        });
    }
}
