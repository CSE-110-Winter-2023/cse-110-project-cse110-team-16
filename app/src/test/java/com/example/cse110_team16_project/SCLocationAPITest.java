package com.example.cse110_team16_project;
import static org.junit.Assert.assertEquals;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;

import com.example.cse110_team16_project.Database.MockResponseBodyBuilder;
import com.example.cse110_team16_project.Database.SCLocationAPI;


import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.rule.GrantPermissionRule;


import com.example.cse110_team16_project.Database.SCLocationDao;
import com.example.cse110_team16_project.Database.SCLocationDatabase;
import com.example.cse110_team16_project.classes.CoordinateClasses.SCLocation;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;


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
            try {
                RecordedRequest request1 = mockWebServer.takeRequest();
                assertEquals(mockWebServer.url("/").toString()+public_code, request1.getRequestUrl().toString());
                String requestBody = request1.getBody().readUtf8();
                SCLocation retrievedLocation = SCLocation.fromJSON(requestBody);
                assertEquals(retrievedLocation.getPublicCode(),public_code);
                assertEquals(retrievedLocation.getLabel(),label);
                assertEquals(retrievedLocation.getLatitude(),3,0.01);
                assertEquals(retrievedLocation.getLongitude(),3,0.01);

            } catch (InterruptedException e) {
                fail();
            }

            String response = new MockResponseBodyBuilder.Get()
                    .addLabel(label)
                    .addLatitude("3")
                    .addLongitude("3")
                    .addPublicCode(public_code)
                    .build();
            mockWebServer.enqueue(new MockResponse().setBody(response));

            SCLocation retrievedLocation = api.getSCLocation(location.getPublicCode());
            try {
                RecordedRequest request1 = mockWebServer.takeRequest();
                assertEquals(mockWebServer.url("/").toString()+public_code, request1.getRequestUrl().toString());
            } catch (InterruptedException e) {
                fail();
            }
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
            try {
                RecordedRequest request1 = mockWebServer.takeRequest();
                assertEquals(mockWebServer.url("/").toString()+public_code, request1.getRequestUrl().toString());
                String requestBody = request1.getBody().readUtf8();
                SCLocation retrievedLocation = SCLocation.fromJSON(requestBody);
                assertEquals(retrievedLocation.getPublicCode(),public_code);
                assertEquals(retrievedLocation.getLabel(),label);
                assertEquals(retrievedLocation.getLatitude(),3,0.01);
                assertEquals(retrievedLocation.getLongitude(),3,0.01);

            } catch (InterruptedException e) {
                fail();
            }
            mockWebServer.enqueue(new MockResponse().setBody(""));
            api.deleteSCLocation(public_code,private_code);
            try {
                RecordedRequest request1 = mockWebServer.takeRequest();
                assertEquals(mockWebServer.url("/").toString()+public_code, request1.getRequestUrl().toString());
                String requestBody = request1.getBody().readUtf8();
                assertTrue(requestBody.contains(private_code));

            } catch (InterruptedException e) {
                fail();
            }
            mockWebServer.enqueue(new MockResponse().setBody(MockResponseBodyBuilder.Not_Found()));
            SCLocation retrievedLocation = api.getSCLocation(location.getPublicCode());
            try {
                RecordedRequest request1 = mockWebServer.takeRequest();
                assertEquals(mockWebServer.url("/").toString()+public_code, request1.getRequestUrl().toString());
            } catch (InterruptedException e) {
                fail();
            }
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
            try {
                RecordedRequest request1 = mockWebServer.takeRequest();
                assertEquals(mockWebServer.url("/").toString()+public_code, request1.getRequestUrl().toString());
                String requestBody = request1.getBody().readUtf8();
                SCLocation retrievedLocation = SCLocation.fromJSON(requestBody);
                assertEquals(retrievedLocation.getPublicCode(),public_code);
                assertEquals(retrievedLocation.getLabel(),label1);
                assertEquals(retrievedLocation.getLatitude(),3,0.01);
                assertEquals(retrievedLocation.getLongitude(),3,0.01);

            } catch (InterruptedException e) {
                fail();
            }
            location.setLabel(label2);
            mockWebServer.enqueue(new MockResponse().setBody(""));
            api.patchSCLocation(location,private_code,false);
            try {
                RecordedRequest request1 = mockWebServer.takeRequest();
                assertEquals(mockWebServer.url("/").toString()+public_code, request1.getRequestUrl().toString());
                String requestBody = request1.getBody().readUtf8();
                SCLocation retrievedLocation = SCLocation.fromJSON(requestBody);
                assertEquals(retrievedLocation.getPublicCode(),public_code);
                assertEquals(retrievedLocation.getLabel(),label2);
                assertEquals(retrievedLocation.getLatitude(),3,0.01);
                assertEquals(retrievedLocation.getLongitude(),3,0.01);

            } catch (InterruptedException e) {
                fail();
            }
            String response = new MockResponseBodyBuilder.Get()
                    .addLabel(label2)
                    .addLatitude("3")
                    .addLongitude("3")
                    .addPublicCode(public_code)
                    .build();
            mockWebServer.enqueue(new MockResponse().setBody(response));
            SCLocation retrievedLocation = api.getSCLocation(location.getPublicCode());
            try {
                RecordedRequest request1 = mockWebServer.takeRequest();
                assertEquals(mockWebServer.url("/").toString()+public_code, request1.getRequestUrl().toString());
            } catch (InterruptedException e) {
                fail();
            }
            assertEquals(retrievedLocation.getLabel(),location.getLabel());
            assertEquals(retrievedLocation.getLatitude(),location.getLatitude(),0.01);
            assertEquals(retrievedLocation.getLongitude(),location.getLongitude(),0.01);
            assertEquals(retrievedLocation.getPublicCode(),location.getPublicCode());
        });
    }

}
