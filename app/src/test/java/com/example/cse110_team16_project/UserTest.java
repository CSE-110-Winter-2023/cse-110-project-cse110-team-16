package com.example.cse110_team16_project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.cse110_team16_project.classes.Coordinates;
import com.example.cse110_team16_project.classes.Degrees;
import com.example.cse110_team16_project.classes.User;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

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
    public void UserGeneralTest(){
        User user = new User(new Coordinates(0, 0), "Calvin");
        assertEquals(new Coordinates(0, 0), user.getCoordinates().getValue());
        assertEquals("Calvin", user.getName());
        user.setCoordinates(new Coordinates(1.1, 2.2));
        assertEquals(new Coordinates(1.1, 2.2), user.getCoordinates().getValue());
    }
    @Test
    public void testCoordinatesNotNull(){
        User user = new User();
        assertEquals(new Coordinates(0, 0),user.getCoordinates().getValue());
        try {
            user.setCoordinates(null);
            assertNotNull(user.getCoordinates().getValue());
        }
        catch(Exception e){
            assertTrue(true);
        }
    }

}

