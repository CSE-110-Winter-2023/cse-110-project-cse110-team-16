package com.example.cse110_team16_project;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

import android.location.Location;

import com.example.cse110_team16_project.classes.Coordinates;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
public class CoordinatesTest {
    @Test
    public void testConstructor(){
        Coordinates c = new Coordinates(1.2345,-2.146);
        assertEquals(1.2345,c.first,0.0001);
        assertEquals(-2.146,c.second,0.0001);
        Coordinates e = new Coordinates(new Location((String) null));
        assertEquals(0,e.first,0.0001);
        assertEquals(0,e.second,0.0001);
    }
    @Test
    public void testbearingTo(){
        Coordinates c = new Coordinates(1,2);
        Coordinates d = new Coordinates(3,2);
        Coordinates e = new Coordinates(3,4);
        assertEquals(0,c.bearingTo(d),0.10);
        assertEquals(90,d.bearingTo(e),0.10);
        assertEquals(45,c.bearingTo(e),0.10);
    }
}