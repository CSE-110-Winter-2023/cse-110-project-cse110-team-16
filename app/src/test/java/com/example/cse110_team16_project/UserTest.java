package com.example.cse110_team16_project;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.cse110_team16_project.classes.Coordinates;
import com.example.cse110_team16_project.classes.User;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
public class UserTest {
    //Tests do not test methods in isolation which is bad practice, but it's a small
    //class so what could possibly go wrong.

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Test
    public void UserTest(){
        User user = new User();
        assertEquals(new Coordinates(0, 0), user.getCoordinates().getValue());
        assertEquals(0.0f, user.getDirection().getValue(), 0.10);
        user.setCoordinates(new Coordinates(1.1, 2.2));
        assertEquals(new Coordinates(1.1, 2.2), user.getCoordinates().getValue());
        user.setDirection(218.66f);
        assertEquals(218.66, user.getDirection().getValue(), 0.01);
    }

}

