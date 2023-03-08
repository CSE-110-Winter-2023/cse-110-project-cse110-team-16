package com.example.cse110_team16_project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;
import androidx.test.rule.GrantPermissionRule;

import com.example.cse110_team16_project.Units.Meters;
import com.example.cse110_team16_project.classes.Constants;
import com.example.cse110_team16_project.classes.Coordinates;
import com.example.cse110_team16_project.classes.DistanceUpdater;
import com.example.cse110_team16_project.Units.Radians;
import com.example.cse110_team16_project.classes.SCLocation;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RunWith(RobolectricTestRunner.class)
public class DistanceUpdaterTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule
            .grant(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Test
    public void testDistanceUpdater(){
        ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(CompassActivity.class);


        Coordinates friendCoordinates1 = new Coordinates(1,1.15056);
        String friendLabel1 = "Victor";
        Coordinates friendCoordinates2 = new Coordinates(2,0);
        String friendLabel2 = "David";
        LiveData<List<SCLocation>> friends = new MutableLiveData<>(new ArrayList<>(Arrays.asList(new SCLocation(friendCoordinates1,friendLabel1, "A123456788"),
                new SCLocation(friendCoordinates2,friendLabel2, "A123456789"))));

        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onActivity(activity ->
        {
            MutableLiveData<Coordinates> userCoordinates = new MutableLiveData<>(new Coordinates());
            MutableLiveData<Radians> userDirection = new MutableLiveData<>(new Radians(0.0));
            DistanceUpdater friendDistanceUpdater = new DistanceUpdater(activity, friends, userCoordinates);
            List<Meters> friendDirections = friendDistanceUpdater.getLastKnownEntityDistancesFromUser().getValue();
            assertNull(friendDirections);
            friendDistanceUpdater.updateAllEntityDistancesFromUser(friends.getValue(),userCoordinates.getValue());
            ArrayList<Double> expected = new ArrayList<>(Arrays.asList(friendDistanceUpdater.
                            getEntityDistanceFromUser(Objects.requireNonNull(userCoordinates.getValue()),friends.getValue().get(0)),
                    friendDistanceUpdater.
                            getEntityDistanceFromUser(userCoordinates.getValue(),friends.getValue().get(1))));
            assertEquals(169500,expected.get(0),1000);  //good enough estimate
            assertEquals(222000,expected.get(1),1000);
            friendDirections = friendDistanceUpdater.getLastKnownEntityDistancesFromUser().getValue();
            assertEquals(expected.get(0),
                    friendDirections.get(0).getMeters(),0.001);
            assertEquals(expected.get(1),
                    friendDirections.get(1).getMeters(),0.001);
        });

    }
}
