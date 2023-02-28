package com.example.cse110_team16_project;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.cse110_team16_project.classes.CoordinateEntity;
import com.example.cse110_team16_project.classes.Coordinates;
import com.example.cse110_team16_project.classes.Degrees;
import com.example.cse110_team16_project.classes.Radians;
import com.example.cse110_team16_project.classes.RelativeDirectionUpdater;
import com.example.cse110_team16_project.classes.User;

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
public class RelativeDirectionUpdaterTest {
    //Tests do not test methods in isolation which is bad practice, but it's a small
    //class so what could possibly go wrong.
    @Rule
    public ActivityScenarioRule<CompassActivity> rule = new ActivityScenarioRule<>(CompassActivity.class);
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Test
    public void testFriendDirectionUpdater(){
        ActivityScenario<CompassActivity> scenario = rule.getScenario();

        Coordinates friendCoordinates1 = new Coordinates(1,1.15056);
        String friendLabel1 = "Victor";
        Coordinates friendCoordinates2 = new Coordinates(2,0);
        String friendLabel2 = "David";
        List<CoordinateEntity> friends = new ArrayList<>(Arrays.asList(new User(friendCoordinates1,friendLabel1),
                new User(friendCoordinates2,friendLabel2)));
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.onActivity(activity ->
        {
            MutableLiveData<Coordinates> userCoordinates = new MutableLiveData<>(new Coordinates(0.0,0.0));
            MutableLiveData<Radians> userDirection = new MutableLiveData<>(new Radians(0.0));
            RelativeDirectionUpdater friendDirectionUpdater = new RelativeDirectionUpdater(activity, friends, userCoordinates, userDirection);
            List<Degrees> friendDirections = friendDirectionUpdater.getLastKnownEntityDirectionsFromUser().getValue();
            assertEquals(0.0, friendDirections.get(0).getDegrees(),0.0001);
            assertEquals(0.0, friendDirections.get(1).getDegrees(),0.0001);
            friendDirectionUpdater.updateAllEntityDirectionsFromUser(userCoordinates.getValue(), userDirection.getValue());
            ArrayList<Degrees> expected = new ArrayList<>(Arrays.asList(friendDirectionUpdater.
                            getEntityDirectionFromUser(Objects.requireNonNull(userCoordinates.getValue()),friends.get(0), new Degrees(0.0)),
                    friendDirectionUpdater.
                            getEntityDirectionFromUser(userCoordinates.getValue(),friends.get(1),new Degrees(0.0))));
            assertEquals(49,expected.get(0).getDegrees(),.1);
            assertEquals(0,expected.get(1).getDegrees(),.1);
            friendDirections = friendDirectionUpdater.getLastKnownEntityDirectionsFromUser().getValue();
            assertEquals(expected.get(0).getDegrees(),
                    friendDirections.get(0).getDegrees(),0.001);
            assertEquals(expected.get(1).getDegrees(),
                    friendDirections.get(1).getDegrees(),0.001);

        });


    }

}