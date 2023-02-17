package com.example.cse110_team16_project;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.cse110_team16_project.classes.Coordinates;
import com.example.cse110_team16_project.classes.Home;
import com.example.cse110_team16_project.classes.HomeDirectionUpdater;
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
public class HomeDirectionUpdaterTest {
    //Tests do not test methods in isolation which is bad practice, but it's a small
    //class so what could possibly go wrong.
    @Rule
    public ActivityScenarioRule<CompassActivity> rule = new ActivityScenarioRule<>(CompassActivity.class);
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Test
    public void testHomeDirectionUpdater(){
        ActivityScenario<CompassActivity> scenario = rule.getScenario();

        User user = new User();
        Coordinates homeCoordinates1 = new Coordinates(1,1.15056);
        String homeLabel1 = "Parents' Home";
        Coordinates homeCoordinates2 = new Coordinates(2,0);
        String homeLabel2 = "Best Friend's Home";
        List<Home> homes = new ArrayList<>(Arrays.asList(new Home(homeCoordinates1,homeLabel1),
                new Home(homeCoordinates2,homeLabel2)));
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.onActivity(activity ->
        {
            HomeDirectionUpdater homeDirectionUpdater = new HomeDirectionUpdater(activity, homes, user);
            assertEquals(new ArrayList<>(Arrays.asList(0f,0f)),homeDirectionUpdater.getLastKnownHomeDirectionsFromUser().getValue());
            homeDirectionUpdater.updateAllHomesDirectionFromUser();
            ArrayList<Float> expected = new ArrayList<>(Arrays.asList(homeDirectionUpdater.
                    getHomeDirectionFromUser(Objects.requireNonNull(user.getCoordinates().getValue()),homes.get(0)),
                    homeDirectionUpdater.
                            getHomeDirectionFromUser(user.getCoordinates().getValue(),homes.get(1))));
            assertEquals(49f,expected.get(0),.1f);
            assertEquals(0f,expected.get(1),.1f);
            assertEquals(expected,
                    homeDirectionUpdater.getLastKnownHomeDirectionsFromUser().getValue());

        });


    }

}