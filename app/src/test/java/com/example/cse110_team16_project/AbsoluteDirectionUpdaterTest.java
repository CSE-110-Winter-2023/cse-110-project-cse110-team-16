package com.example.cse110_team16_project;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;
import androidx.test.rule.GrantPermissionRule;

import com.example.cse110_team16_project.classes.Coordinates;
import com.example.cse110_team16_project.Units.Degrees;
import com.example.cse110_team16_project.Units.Radians;
import com.example.cse110_team16_project.classes.AbsoluteDirectionUpdater;
import com.example.cse110_team16_project.classes.SCLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
public class AbsoluteDirectionUpdaterTest {
    //Tests do not test methods in isolation which is bad practice, but it's a small
    //class so what could possibly go wrong.

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule
            .grant(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION);
    @Test
    public void testAbsoluteDirectionUpdater(){
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
            MutableLiveData<Coordinates> userCoordinates = new MutableLiveData<>(new Coordinates(0.0,0.0));
            MutableLiveData<Radians> userDirection = new MutableLiveData<>(new Radians(0.0));
            AbsoluteDirectionUpdater friendDirectionUpdater = new AbsoluteDirectionUpdater(activity, friends, userCoordinates);
            List<Degrees> friendDirections = friendDirectionUpdater.getLastKnownEntityDirectionsFromUser().getValue();
            assertNull(friendDirections);
            friendDirectionUpdater.updateAllEntityDirectionsFromUser(friends.getValue(),userCoordinates.getValue());
            ArrayList<Degrees> expected = new ArrayList<>(Arrays.asList(friendDirectionUpdater.
                            getEntityDirectionFromUser(Objects.requireNonNull(userCoordinates.getValue()),friends.getValue().get(0)),
                    friendDirectionUpdater.
                            getEntityDirectionFromUser(userCoordinates.getValue(),friends.getValue().get(1))));
            assertEquals(49,expected.get(0).getDegrees(),.3);
            assertEquals(0,expected.get(1).getDegrees(),.3);
            friendDirections = friendDirectionUpdater.getLastKnownEntityDirectionsFromUser().getValue();
            assertEquals(expected.get(0).getDegrees(),
                    friendDirections.get(0).getDegrees(),0.001);
            assertEquals(expected.get(1).getDegrees(),
                    friendDirections.get(1).getDegrees(),0.001);

        });


    }

}