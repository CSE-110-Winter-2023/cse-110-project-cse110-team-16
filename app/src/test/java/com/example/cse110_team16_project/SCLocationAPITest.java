package com.example.cse110_team16_project;
import static org.junit.Assert.assertEquals;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;

import com.example.cse110_team16_project.Database.SCLocationAPI;


import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


import androidx.test.rule.GrantPermissionRule;


import com.example.cse110_team16_project.classes.SCLocation;

import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class SCLocationAPITest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule
            .grant(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION);

    //Yes I know it's bad practice to not mock the server and stuff but I got things to do man
    @Test
    public void testRealLiveAPIPutGet() {
        var scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(activity -> {
            SCLocationAPI api = new SCLocationAPI().provide();
            String private_code = "amongusnoonewilleverhavethisasacode";
            SCLocation location = new SCLocation(3,3,"testlabel","6969696969696");
            api.putSCLocation(location,private_code);
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
            SCLocationAPI api = new SCLocationAPI().provide();
            String private_code = "amongusnoonewilleverhavethisasacode";
            SCLocation location = new SCLocation(3,3,"testlabel","6969696969696");
            api.deleteSCLocation(location.public_code,private_code);
            SCLocation retrievedLocation = api.getSCLocation(location.getPublicCode());
            assertNull(retrievedLocation);
        });
    }

    @Test
    public void testRealLiveAPIPutPatchGet() {
        var scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(activity -> {
            SCLocationAPI api = new SCLocationAPI().provide();
            String private_code = "amongusnoonewilleverhavethisasacode";
            SCLocation location = new SCLocation(3,3,"testlabel","6969696969696");
            location.setLabel("testUpdateLabel");
            api.patchSCLocation(location,private_code,false);
            SCLocation retrievedLocation = api.getSCLocation(location.getPublicCode());
            assertEquals(retrievedLocation.getLabel(),location.getLabel());
            assertEquals(retrievedLocation.getLatitude(),location.getLatitude(),0.01);
            assertEquals(retrievedLocation.getLongitude(),location.getLongitude(),0.01);
            assertEquals(retrievedLocation.getPublicCode(),location.getPublicCode());
        });
    }

}
