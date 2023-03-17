package com.example.cse110_team16_project.ScenarioTests;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.example.cse110_team16_project.Database.MockResponseBuilder;
import com.example.cse110_team16_project.Database.SCLocationDao;
import com.example.cse110_team16_project.Database.SCLocationDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;


import com.example.cse110_team16_project.Database.SCLocationRepository;
import com.example.cse110_team16_project.ListActivity;
import com.example.cse110_team16_project.R;
import com.example.cse110_team16_project.classes.CoordinateClasses.SCLocation;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@RunWith(RobolectricTestRunner.class)
public class Story1ScenarioTest {

    private final int WAIT_FOR_ROOM_TIME = 1500;
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
    public void Scenario1() {
        var scenario = ActivityScenario.launch(ListActivity.class);
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onActivity(activity -> {
            SCLocationRepository repository = new SCLocationRepository(db.getDao(),mockWebServer.url("/").toString());
            String private_code = "Story1Scenario1Private";
            String public_code = "Story1Scenario1Public";
            String label = "testLabel";
            SCLocation location = new SCLocation(3,3,label,public_code);
            String response = new MockResponseBuilder.Get()
                    .addLabel(label)
                    .addLatitude("3")
                    .addLongitude("3")
                    .addPublicCode(public_code)
                    .build();
            mockWebServer.enqueue(new MockResponse().setBody(response));
            mockWebServer.enqueue(new MockResponse().setBody(response));
            mockWebServer.enqueue(new MockResponse().setBody(response));
            repository.upsertRemote(location,private_code);
            List<SCLocation> beforeLocationList = dao.getAll();
            EditText newLocationText = activity.findViewById(R.id.input_new_location_code);
            newLocationText.requestFocus();
            newLocationText.setText(location.getPublicCode());
            newLocationText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
            newLocationText.clearFocus();
            try {
                Thread.sleep(WAIT_FOR_ROOM_TIME);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            List<SCLocation> afterLocationList = dao.getAll();
            assertEquals(beforeLocationList.size() + 1, afterLocationList.size());
        });
    }

    @Test
    public void Scenario2() {
        var scenario = ActivityScenario.launch(ListActivity.class);
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onActivity(activity -> {
            SCLocationRepository repository = new SCLocationRepository(dao,mockWebServer.url("/").toString());
            String private_code = "Story1Scenario2Private";
            String public_code = "Story1Scenario2Public";
            String label = "testLabel";
            SCLocation location = new SCLocation(3,3,label,public_code);
            mockWebServer.enqueue(new MockResponse().setBody(""));
            repository.deleteRemote(location.public_code,private_code);
            List<SCLocation> beforeLocationList = dao.getAll();
            EditText newLocationText = activity.findViewById(R.id.input_new_location_code);
            newLocationText.requestFocus();
            newLocationText.setText(location.getPublicCode());
            newLocationText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
            mockWebServer.enqueue(new MockResponse().setBody(""));
            mockWebServer.enqueue(new MockResponse().setBody(""));
            newLocationText.clearFocus();
            List<SCLocation> afterLocationList = dao.getAll();
            assertEquals(beforeLocationList.size(), afterLocationList.size());
        });
    }
}
