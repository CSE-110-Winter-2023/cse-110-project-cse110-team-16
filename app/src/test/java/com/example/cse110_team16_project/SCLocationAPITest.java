package com.example.cse110_team16_project;
import static org.junit.Assert.assertEquals;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;

import com.example.cse110_team16_project.Database.MockResponseBuilder;
import com.example.cse110_team16_project.Database.SCLocationAPI;


import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
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
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;


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

    @Rule
    public MockWebServer mockWebServer = new MockWebServer();

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

    @Test
    public void testFakeAPIPutGet() {
        var scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(activity -> {
            SCLocationAPI api = SCLocationAPI.provide();
            api.setUrl(mockWebServer.url("/").toString());
            String private_code = "SCLocationAPITest1Private";
            String public_code = "SCLocationAPITest1Public";
            String label = "testLabel";
            SCLocation location = new SCLocation(3,3,label,public_code);
            mockWebServer.enqueue(new MockResponse().setBody(""));
            api.putSCLocation(location,private_code);

            String response = new MockResponseBuilder.Get()
                    .addLabel(label)
                    .addLatitude("3")
                    .addLongitude("3")
                    .addPublicCode(public_code)
                    .build();
            mockWebServer.enqueue(new MockResponse().setBody(response));

            SCLocation retrievedLocation = api.getSCLocation(location.getPublicCode());
            assertEquals(retrievedLocation.getLabel(),location.getLabel());
            assertEquals(retrievedLocation.getLatitude(),location.getLatitude(),0.01);
            assertEquals(retrievedLocation.getLongitude(),location.getLongitude(),0.01);
            assertEquals(retrievedLocation.getPublicCode(),location.getPublicCode());
        });
    }

    @Test
    public void testFakeAPIPutDeleteGet() {
        var scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(activity -> {
            SCLocationAPI api = SCLocationAPI.provide();
            api.setUrl(mockWebServer.url("/").toString());
            String private_code = "SCLocationAPITest2Private";
            String public_code = "SCLocationAPITest2Public";
            String label = "testLabel";
            SCLocation location = new SCLocation(3,3,label,public_code);
            mockWebServer.enqueue(new MockResponse().setBody(""));
            api.putSCLocation(location,private_code);
            mockWebServer.enqueue(new MockResponse().setBody(""));
            api.deleteSCLocation(public_code,private_code);
            mockWebServer.enqueue(new MockResponse().setBody(""));
            SCLocation retrievedLocation = api.getSCLocation(location.getPublicCode());
            assertNull(retrievedLocation);
        });
    }

    @Test
    public void testFakeAPIPutPatchGet() {
        var scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(activity -> {
            SCLocationAPI api = SCLocationAPI.provide();
            api.setUrl(mockWebServer.url("/").toString());
            String private_code = "SCLocationAPITest1Private";
            String public_code = "SCLocationAPITest1Public";
            String label1 = "testLabel1";
            String label2 = "testLabel2";
            SCLocation location = new SCLocation(3,3,label1,public_code);
            mockWebServer.enqueue(new MockResponse().setBody(""));
            api.putSCLocation(location,private_code);
            location.setLabel(label2);
            mockWebServer.enqueue(new MockResponse().setBody(""));
            api.patchSCLocation(location,private_code,false);
            String response = new MockResponseBuilder.Get()
                    .addLabel(label2)
                    .addLatitude("3")
                    .addLongitude("3")
                    .addPublicCode(public_code)
                    .build();
            mockWebServer.enqueue(new MockResponse().setBody(response));
            SCLocation retrievedLocation = api.getSCLocation(location.getPublicCode());
            assertEquals(retrievedLocation.getLabel(),location.getLabel());
            assertEquals(retrievedLocation.getLatitude(),location.getLatitude(),0.01);
            assertEquals(retrievedLocation.getLongitude(),location.getLongitude(),0.01);
            assertEquals(retrievedLocation.getPublicCode(),location.getPublicCode());
        });
    }

}
