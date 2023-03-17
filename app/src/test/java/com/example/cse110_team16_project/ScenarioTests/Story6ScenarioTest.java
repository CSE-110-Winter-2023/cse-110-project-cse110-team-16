package com.example.cse110_team16_project.ScenarioTests;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.example.cse110_team16_project.CompassActivity;
import com.example.cse110_team16_project.Database.MockResponseBuilder;
import com.example.cse110_team16_project.Database.SCLocationDao;
import com.example.cse110_team16_project.Database.SCLocationDatabase;
import com.example.cse110_team16_project.Database.SCLocationRepository;
import com.example.cse110_team16_project.classes.CoordinateClasses.Coordinates;
import com.example.cse110_team16_project.classes.CoordinateClasses.SCLocation;
import com.example.cse110_team16_project.classes.DeviceInfo.UserLocationSync;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@RunWith(RobolectricTestRunner.class)
public class Story6ScenarioTest {
    private SCLocationDao dao;
    private SCLocationDatabase db;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

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
    public void Story6Scenario1Test(){
        var scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(activity -> {
            SCLocationRepository repository = new SCLocationRepository(dao,mockWebServer.url("/").toString());
            String label = "Mom";
            String private_code = "Story6Scenario1TestPrivate";
            String public_code = "Story6Scenario1TestPublic";
            SCLocation location = new SCLocation(0,0,label,public_code);
            String response = new MockResponseBuilder.Get()
                    .addLabel(label)
                    .addLatitude("0")
                    .addLongitude("0")
                    .addPublicCode(public_code)
                    .build();
            mockWebServer.enqueue(new MockResponse().setBody(response));
            repository.upsertRemote(location,private_code);
            Coordinates coords = new Coordinates(2,2);
            MutableLiveData<Coordinates> liveCoordinates = new MutableLiveData<>();
            response = new MockResponseBuilder.Get()
                    .addLabel(label)
                    .addLatitude("2")
                    .addLongitude("2")
                    .addPublicCode(public_code)
                    .build();
            mockWebServer.enqueue(new MockResponse().setBody(response));
            mockWebServer.enqueue(new MockResponse().setBody(response));
            mockWebServer.enqueue(new MockResponse().setBody(response));
            mockWebServer.enqueue(new MockResponse().setBody(response));
            UserLocationSync syncher = new UserLocationSync(liveCoordinates,
                    new SCLocation(label,public_code),private_code,activity,repository);
            liveCoordinates.postValue(coords);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            mockWebServer.enqueue(new MockResponse().setBody(response));
            SCLocation updatedLocation = repository.getRemote(public_code);
            assertEquals(2,updatedLocation.getLatitude(),0.01);
            assertEquals(2,updatedLocation.getLongitude(),0.01);
        });
    }
}
