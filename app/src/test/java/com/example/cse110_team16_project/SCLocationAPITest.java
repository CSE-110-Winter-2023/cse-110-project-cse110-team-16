package com.example.cse110_team16_project;
import static org.junit.Assert.assertEquals;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;

import com.example.cse110_team16_project.Database.SCLocationAPI;


import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.rule.GrantPermissionRule;


import com.example.cse110_team16_project.Database.SCLocationDao;
import com.example.cse110_team16_project.Database.SCLocationDatabase;
import com.example.cse110_team16_project.classes.CoordinateClasses.SCLocation;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class SCLocationAPITest {

    private final int WAIT_FOR_UPDATE_TIME = 1500;

    private SCLocationDao dao;
    private SCLocationDatabase db;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule
            .grant(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION);
    @Before
    public void createDb(){
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, SCLocationDatabase.class)
                .allowMainThreadQueries()
                .build();
        dao = db.getDao();
        SCLocationDatabase.inject(db);
    }

    @After
    public void closeDb() throws Exception {
        db.close();
    }

    //Yes I know it's bad practice to not mock the server and stuff but I got things to do man
    @Test
    public void testRealLiveAPIPutGet() {
        var scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(activity -> {
            SCLocationAPI api = SCLocationAPI.provide();
            String private_code = "SCLocationAPITest1Private";
            String public_code = "SCLocationAPITest1Public";
            String label = "testLabel";
            SCLocation location = new SCLocation(3,3,label,public_code);
            api.putSCLocation(location,private_code);
            try {
                Thread.sleep(WAIT_FOR_UPDATE_TIME);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            SCLocation retrievedLocation = api.getSCLocation(location.getPublicCode());
            assertEquals(retrievedLocation.getLabel(),location.getLabel());
            assertEquals(retrievedLocation.getLatitude(),location.getLatitude(),0.01);
            assertEquals(retrievedLocation.getLongitude(),location.getLongitude(),0.01);
            assertEquals(retrievedLocation.getPublicCode(),location.getPublicCode());
        });
    }

    @Test
    public void testRealLiveAPIPutDeleteGet() {
        var scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(activity -> {
            SCLocationAPI api = SCLocationAPI.provide();
            String private_code = "SCLocationAPITest2Private";
            String public_code = "SCLocationAPITest2Public";
            String label = "testLabel";
            SCLocation location = new SCLocation(3,3,label,public_code);
            api.putSCLocation(location,private_code);
            try {
                Thread.sleep(WAIT_FOR_UPDATE_TIME);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            api.deleteSCLocation(public_code,private_code);
            SCLocation retrievedLocation = api.getSCLocation(location.getPublicCode());
            assertNull(retrievedLocation);
        });
    }

    @Test
    public void testRealLiveAPIPutPatchGet() {
        var scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(activity -> {
            SCLocationAPI api = SCLocationAPI.provide();
            String private_code = "SCLocationAPITest1Private";
            String public_code = "SCLocationAPITest1Public";
            String label1 = "testLabel1";
            String label2 = "testLabel2";
            SCLocation location = new SCLocation(3,3,label1,public_code);
            api.putSCLocation(location,private_code);
            try {
                Thread.sleep(WAIT_FOR_UPDATE_TIME);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            location.setLabel(label2);
            api.patchSCLocation(location,private_code,false);
            SCLocation retrievedLocation = api.getSCLocation(location.getPublicCode());
            assertEquals(retrievedLocation.getLabel(),location.getLabel());
            assertEquals(retrievedLocation.getLatitude(),location.getLatitude(),0.01);
            assertEquals(retrievedLocation.getLongitude(),location.getLongitude(),0.01);
            assertEquals(retrievedLocation.getPublicCode(),location.getPublicCode());
        });
    }

}
