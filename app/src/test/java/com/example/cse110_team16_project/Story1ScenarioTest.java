package com.example.cse110_team16_project;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

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
import com.example.cse110_team16_project.classes.CoordinateClasses.SCLocation;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class Story1ScenarioTest {

    private final int WAIT_FOR_ROOM_TIME = 1500;
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
            SCLocationRepository repository = new SCLocationRepository(db.getDao());
            String private_code = "Story1Scenario1Private";
            String public_code = "Story1Scenario1Public";
            String label = "testLabel";
            SCLocation location = new SCLocation(3,3,label,public_code);
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
            SCLocationRepository repository = new SCLocationRepository(dao);
            String private_code = "Story1Scenario2Private";
            String public_code = "Story1Scenario2Public";
            String label = "testLabel";
            SCLocation location = new SCLocation(3,3,label,public_code);
            repository.deleteRemote(location.public_code,private_code);
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
            assertEquals(beforeLocationList.size(), afterLocationList.size());
        });
    }
}
