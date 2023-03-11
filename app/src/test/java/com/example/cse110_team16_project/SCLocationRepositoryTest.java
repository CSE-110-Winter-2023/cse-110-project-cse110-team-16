package com.example.cse110_team16_project;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.*;


import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.rule.GrantPermissionRule;


import com.example.cse110_team16_project.Database.SCLocationAPI;
import com.example.cse110_team16_project.Database.SCLocationDao;
import com.example.cse110_team16_project.Database.SCLocationDatabase;
import com.example.cse110_team16_project.Database.SCLocationRepository;
import com.example.cse110_team16_project.classes.Coordinates;
import com.example.cse110_team16_project.classes.SCLocation;

import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RunWith(RobolectricTestRunner.class)
public class SCLocationRepositoryTest {
    private SCLocationDao dao;
    private SCLocationDatabase db;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void createDb(){
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, SCLocationDatabase.class)
                .allowMainThreadQueries()
                .build();
        dao = db.getDao();
    }

    @After
    public void closeDb() throws Exception {
        db.close();
    }

    //Yes I know it's bad practice to not mock the server and stuff but I got things to do man
    @Test
    public void testRealUpsertGetRemote() {
        var scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(activity -> {
            SCLocationRepository repository = new SCLocationRepository(SCLocationDatabase.provide(activity).getDao());
            String private_code = "amongusnoonewilleverhavethisasacode";
            SCLocation location = new SCLocation(3,3,"testlabel","6969696969696");
            repository.upsertRemote(location,private_code);
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
            scenario.moveToState(Lifecycle.State.STARTED);
            scenario.onActivity(activity -> {
            SCLocationRepository repository = new SCLocationRepository(dao);
            SCLocation scLocation1 = new SCLocation(2,2,"Mom","wadadawdabdbawbdwadawdwada");
            SCLocation scLocation3 = new SCLocation(4,4,"Momn't","wadadawdabdbawbdwadawdwada");

            repository.upsertLocal(scLocation1);
            repository.upsertLocal(scLocation3);
            assertTrue(repository.existsLocal(scLocation1.getPublicCode()));
            assertFalse(repository.existsRemote(scLocation1.getPublicCode()));
        });
    }

    @Test
    public void testUpsertDeleteExistsLocal(){
        var scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(activity -> {
            SCLocationRepository repository = new SCLocationRepository(dao);
            SCLocation scLocation1 = new SCLocation(2,2,"Mom","wadadawdabdbawbdwadawdwada");

            repository.upsertLocal(scLocation1);
            repository.deleteLocal(scLocation1);
            assertFalse(repository.existsLocal(scLocation1.getPublicCode()));
        });
    }

    @Test
    public void testGetSynced(){
        var scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(activity -> {
            SCLocationRepository repository = new SCLocationRepository(dao);
            SCLocation location = new SCLocation(2,2,"Mom","wadadawdabdbawbdwadawdwada");

            Future<Void> future = repository.upsertRemote(location,location.public_code);
            try {
                future.get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            LiveData<SCLocation> retrievedLocationLive = repository.getSynced(location.public_code);
            try {
                Thread.sleep(1000); //ew
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            retrievedLocationLive.observe(activity,(retrievedNull) -> {
                retrievedLocationLive.removeObservers(activity);
                retrievedLocationLive.observe(activity,(retrievedLocation) -> {
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
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(activity -> {
            SCLocationRepository repository = new SCLocationRepository(dao);
            SCLocation location = new SCLocation(2,2,"Mom","wadadawdabdbawbdwadawdwada");

            Future<Void> future = repository.upsertRemote(location,location.public_code);
            try {
                future.get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            LiveData<SCLocation> retrievedLocationLive = repository.getRemoteLive(location.public_code);
            try {
                Thread.sleep(1000); //ew
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            retrievedLocationLive.observe(activity,(retrievedNull) -> {
                retrievedLocationLive.removeObservers(activity);
                retrievedLocationLive.observe(activity,(retrievedLocation) -> {
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
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(activity -> {
            SCLocationRepository repository = new SCLocationRepository(dao);
            SCLocation location = new SCLocation(2,2,"Mom","wadadawdabdbawbdwadawdwada");

            MutableLiveData<SCLocation> liveLocation = new MutableLiveData<>();
            liveLocation.postValue(location);
            repository.updateSCLocationLive(liveLocation,location.public_code);
            try {
                Thread.sleep(1000); //ew
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            SCLocation retrievedLocation = repository.getRemote(location.getPublicCode());
            assertEquals(retrievedLocation.getLabel(), location.getLabel());
            assertEquals(retrievedLocation.getLatitude(), location.getLatitude(), 0.01);
            assertEquals(retrievedLocation.getLongitude(), location.getLongitude(), 0.01);
            assertEquals(retrievedLocation.getPublicCode(), location.getPublicCode());

            location.setCoordinates(new Coordinates(3,3));
            liveLocation.postValue(location);
            try {
                Thread.sleep(4000); //ew
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            retrievedLocation = repository.getRemote(location.getPublicCode());
            assertEquals(retrievedLocation.getLabel(), location.getLabel());
            assertEquals(retrievedLocation.getLatitude(), location.getLatitude(), 0.01);
            assertEquals(retrievedLocation.getLongitude(), location.getLongitude(), 0.01);
            assertEquals(retrievedLocation.getPublicCode(), location.getPublicCode());
        });
    }
}
