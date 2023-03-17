package com.example.cse110_team16_project;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.annotation.LooperMode.Mode.PAUSED;


import android.content.Context;
import android.os.Looper;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;


import com.example.cse110_team16_project.Database.MockResponseBodyBuilder;
import com.example.cse110_team16_project.Database.SCLocationDao;
import com.example.cse110_team16_project.Database.SCLocationDatabase;
import com.example.cse110_team16_project.Database.SCLocationRepository;
import com.example.cse110_team16_project.classes.CoordinateClasses.Coordinates;
import com.example.cse110_team16_project.classes.CoordinateClasses.SCLocation;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.LooperMode;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

@RunWith(RobolectricTestRunner.class)
public class SCLocationRepositoryTest {
    private SCLocationDao dao;
    private SCLocationDatabase db;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Rule
    public MockWebServer mockWebServer = new MockWebServer();

    @Before
    public void createDb() throws IOException {
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
        mockWebServer.shutdown();
    }

    @Test
    public void testFakeUpsertGetRemote() {
        var scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onActivity(activity -> {
            SCLocationRepository repository = new SCLocationRepository(dao,mockWebServer.url("/").toString());
            String private_code = "SCLocationRepositoryTest1Private";
            String public_code = "SCLocationRepositoryTest1Public";
            String label = "testLabel";
            SCLocation location = new SCLocation(3,3,label,public_code);
            final Dispatcher dispatcher = new Dispatcher() {
                @Override
                public MockResponse dispatch (RecordedRequest request) throws InterruptedException {
                    return new MockResponse().setBody("");
                }
            };
            mockWebServer.setDispatcher(dispatcher);
            repository.upsertRemote(location,private_code);
            String response = new MockResponseBodyBuilder.Get()
                    .addLabel(label)
                    .addLatitude("3")
                    .addLongitude("3")
                    .addPublicCode(public_code)
                    .build();
            final Dispatcher dispatcher1 = new Dispatcher() {
                @Override
                public MockResponse dispatch (RecordedRequest request) throws InterruptedException {
                    return new MockResponse().setBody(response);
                }
            };
            mockWebServer.setDispatcher(dispatcher1);
            SCLocation retrievedLocation = repository.getRemote(location.getPublicCode());
            assertEquals(retrievedLocation.getLabel(),location.getLabel());
            assertEquals(retrievedLocation.getLatitude(),location.getLatitude(),0.01);
            assertEquals(retrievedLocation.getLongitude(),location.getLongitude(),0.01);
            assertEquals(retrievedLocation.getPublicCode(),location.getPublicCode());
        });
    }

    //Is it too many tests in one place? Yes it is stop judging me imaginary person reading this
    @Test
    public void testUpsertExistsLocalExistsRemote(){
        var scenario = ActivityScenario.launch(CompassActivity.class);
            scenario.moveToState(Lifecycle.State.RESUMED);
            scenario.onActivity(activity -> {
            SCLocationRepository repository = new SCLocationRepository(dao,mockWebServer.url("/").toString());
            String public_code = "SCLocationRepositoryTest2Public";
            String private_code = "SCLocationRepositoryTest2Private";
            SCLocation scLocation1 = new SCLocation(2,2,"testLabel1",public_code);
            SCLocation scLocation3 = new SCLocation(4,4,"testLabel2",public_code);

            repository.upsertLocal(scLocation1);
            repository.upsertLocal(scLocation3);
                final Dispatcher dispatcher = new Dispatcher() {
                    @Override
                    public MockResponse dispatch (RecordedRequest request) throws InterruptedException {
                        return new MockResponse().setBody("");
                    }
                };
                mockWebServer.setDispatcher(dispatcher);
            repository.deleteRemote(scLocation3.getPublicCode(), private_code);
            assertTrue(repository.existsLocal(scLocation1.getPublicCode()));
            assertFalse(repository.existsRemote(scLocation1.getPublicCode()));
        });
    }

    @Test
    public void testUpsertDeleteExistsLocal(){
        var scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onActivity(activity -> {
            SCLocationRepository repository = new SCLocationRepository(dao,mockWebServer.url("/").toString());
            String public_code = "SCLocationRepositoryTest3Public";
            String label = "testLabel";
            SCLocation scLocation1 = new SCLocation(2,2,label,public_code);
            repository.upsertLocal(scLocation1);
            repository.deleteLocal(scLocation1);
            boolean exists = repository.existsLocal(scLocation1.getPublicCode());
            assertFalse(exists);
        });
    }

    @Test
    public void testGetSynced(){
        var scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onActivity(activity -> {
            SCLocationRepository repository = new SCLocationRepository(dao,mockWebServer.url("/").toString());
            String public_code = "SCLocationRepositoryTest3Public";
            String label = "testLabel";

            SCLocation location = new SCLocation(2,2,label,public_code);
            String response = new MockResponseBodyBuilder.Get()
                    .addLabel(label)
                    .addLatitude("2")
                    .addLongitude("2")
                    .addPublicCode(public_code)
                    .build();
            final Dispatcher dispatcher = new Dispatcher() {
                @Override
                public MockResponse dispatch (RecordedRequest request) throws InterruptedException {
                    return new MockResponse().setBody(response);
                }
            };
            mockWebServer.setDispatcher(dispatcher);
            repository.upsertRemote(location,location.public_code);

            LiveData<SCLocation> retrievedLocationLive = repository.getSynced(location.public_code);
            retrievedLocationLive.observe(activity,(retrievedNull) -> {
                retrievedLocationLive.removeObservers(activity);
                retrievedLocationLive.observe(activity,(retrievedLocation) -> {
                    retrievedLocationLive.removeObservers(activity);
                    assertEquals(retrievedLocation.getLabel(), location.getLabel());
                    assertEquals(retrievedLocation.getLatitude(), location.getLatitude(), 0.01);
                    assertEquals(retrievedLocation.getLongitude(), location.getLongitude(), 0.01);
                    assertEquals(retrievedLocation.getPublicCode(), location.getPublicCode());
                });
            });
        });
    }

    @Test
    public void testGetRemoteLive(){
        var scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onActivity(activity -> {
            SCLocationRepository repository = new SCLocationRepository(dao,mockWebServer.url("/").toString());
            String public_code = "SCLocationRepositoryTest4Public";
            String label = "testLabel";
            SCLocation location = new SCLocation(2,2,label,public_code);
            String response = new MockResponseBodyBuilder.Get()
                    .addLabel(label)
                    .addLatitude("2")
                    .addLongitude("2")
                    .addPublicCode(public_code)
                    .build();
            final Dispatcher dispatcher = new Dispatcher() {
                @Override
                public MockResponse dispatch (RecordedRequest request) throws InterruptedException {
                    return new MockResponse().setBody(response);
                }
            };
            mockWebServer.setDispatcher(dispatcher);
            repository.upsertRemote(location,location.public_code);

            LiveData<SCLocation> retrievedLocationLive = repository.getRemoteLive(location.public_code);
            retrievedLocationLive.observe(activity,(retrievedNull) -> {
                retrievedLocationLive.removeObservers(activity);
                retrievedLocationLive.observe(activity,(retrievedLocation) -> {
                    retrievedLocationLive.removeObservers(activity);
                    assertEquals(retrievedLocation.getLabel(), location.getLabel());
                    assertEquals(retrievedLocation.getLatitude(), location.getLatitude(), 0.01);
                    assertEquals(retrievedLocation.getLongitude(), location.getLongitude(), 0.01);
                    assertEquals(retrievedLocation.getPublicCode(), location.getPublicCode());
                });
            });
        });
    }

    @Test
    public void testUpdateSCLocationLive(){
        var scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onActivity(activity -> {
            SCLocationRepository repository = new SCLocationRepository(dao,mockWebServer.url("/").toString());
            String public_code = "SCLocationRepositoryTest5Public";
            String private_code = "SCLocationRepositoryTest5Private";
            String label = "testLabel";
            SCLocation location = new SCLocation(0,0,label,public_code);
            String response = new MockResponseBodyBuilder.Get()
                    .addLabel(label)
                    .addLatitude("0")
                    .addLongitude("0")
                    .addPublicCode(public_code)
                    .build();
            String finalResponse = response;
            final Dispatcher dispatcher1 = new Dispatcher() {
                @Override
                public MockResponse dispatch (RecordedRequest request) throws InterruptedException {
                    return new MockResponse().setBody(finalResponse);
                }
            };
            mockWebServer.setDispatcher(dispatcher1);
            repository.upsertRemote(location,private_code);

            MutableLiveData<SCLocation> liveLocation = new MutableLiveData<>();
            liveLocation.postValue(location);

            repository.updateSCLocationLive(liveLocation,private_code);

            SCLocation retrievedLocation = repository.getRemote(location.getPublicCode());
            assertEquals(retrievedLocation.getLabel(), location.getLabel());
            assertEquals(retrievedLocation.getLatitude(), location.getLatitude(), 0.01);
            assertEquals(retrievedLocation.getLongitude(), location.getLongitude(), 0.01);
            assertEquals(retrievedLocation.getPublicCode(), location.getPublicCode());

            location.setCoordinates(new Coordinates(3,3));
            liveLocation.postValue(location);
            response = new MockResponseBodyBuilder.Get()
                    .addLabel(label)
                    .addLatitude("3")
                    .addLongitude("3")
                    .addPublicCode(public_code)
                    .build();
            String finalResponse1 = response;
            final Dispatcher dispatcher = new Dispatcher() {
                @Override
                public MockResponse dispatch (RecordedRequest request) throws InterruptedException {
                    return new MockResponse().setBody(finalResponse1);
                }
            };
            mockWebServer.setDispatcher(dispatcher);
            retrievedLocation = repository.getRemote(location.getPublicCode());
            assertEquals(retrievedLocation.getLabel(), location.getLabel());
            assertEquals(retrievedLocation.getLatitude(), location.getLatitude(), 0.01);
            assertEquals(retrievedLocation.getLongitude(), location.getLongitude(), 0.01);
            assertEquals(retrievedLocation.getPublicCode(), location.getPublicCode());
        });
    }
}
