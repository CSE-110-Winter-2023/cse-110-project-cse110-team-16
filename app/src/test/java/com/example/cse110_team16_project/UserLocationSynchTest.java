package com.example.cse110_team16_project;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.example.cse110_team16_project.Database.SCLocationDao;
import com.example.cse110_team16_project.Database.SCLocationDatabase;
import com.example.cse110_team16_project.Database.SCLocationRepository;
import com.example.cse110_team16_project.classes.Coordinates;
import com.example.cse110_team16_project.classes.SCLocation;
import com.example.cse110_team16_project.classes.UserLocationSynch;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RunWith(RobolectricTestRunner.class)
public class UserLocationSynchTest {
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

    @Test
    public void Story6Scenario1Test(){
        var scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(activity -> {
            SCLocationRepository repository = new SCLocationRepository(dao);
            String label = "Mom";
            String code = "Story6Scenario1Test";
            SCLocation location = new SCLocation(0,0,label,code);
            repository.upsertRemote(location,code);
            Coordinates coords = new Coordinates(2,2);
            MutableLiveData<Coordinates> liveCoordinates = new MutableLiveData<>(null);
            UserLocationSynch syncher = new UserLocationSynch(liveCoordinates,
                    new SCLocation(label,code),code,activity,repository);
            liveCoordinates.postValue(coords);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            SCLocation updatedLocation = repository.getRemote(code);
            assertEquals(2,updatedLocation.getLatitude(),0.01);
            assertEquals(2,updatedLocation.getLongitude(),0.01);

        });
    }
}
