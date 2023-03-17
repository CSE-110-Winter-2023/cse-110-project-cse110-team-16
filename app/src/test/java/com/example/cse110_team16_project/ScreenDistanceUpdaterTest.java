package com.example.cse110_team16_project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.app.Application;
import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.example.cse110_team16_project.Database.SCLocationDao;
import com.example.cse110_team16_project.Database.SCLocationDatabase;
import com.example.cse110_team16_project.Database.SCLocationRepository;
import com.example.cse110_team16_project.classes.Updaters.ScreenDistanceUpdater;
import com.example.cse110_team16_project.classes.Units.Meters;
import com.example.cse110_team16_project.classes.Misc.Converters;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;

import okhttp3.mockwebserver.MockWebServer;

@RunWith(RobolectricTestRunner.class)
public class ScreenDistanceUpdaterTest {
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
    public void testFindScreenDistance(){
        ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(CompassActivity.class);

        Application application = RuntimeEnvironment.getApplication();
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onActivity(activity ->
        {
            SCLocationRepository repository = new SCLocationRepository(dao,mockWebServer.url("/").toString());

            List<Meters> distances = new ArrayList<>();
            distances.add(new Meters(300)); //.186411 miles
            distances.add(new Meters(2000));  //1.24274 miles
            distances.add(new Meters(70)); // 0.043496
            distances.add(new Meters(5000000)); // 3106.855961
            distances.add(new Meters(50000)); // 31.06856

            ScreenDistanceUpdater updater = new ScreenDistanceUpdater(activity,3);
            List<Double> requiredDistances = updater.findScreenDistance(distances);
            assertEquals(Converters.metersToMiles(new Meters(300)).getMiles()*
                    ScreenDistanceUpdater.LARGEST_RADIUS/3,requiredDistances.get(0),0.1);
            assertEquals((Converters.metersToMiles(new Meters(2000)).getMiles()/9 + 1)*
                    ScreenDistanceUpdater.LARGEST_RADIUS/3,requiredDistances.get(1),0.1);
            assertEquals((Converters.metersToMiles(new Meters(70)).getMiles()/1 + 0)*
                    ScreenDistanceUpdater.LARGEST_RADIUS/3,requiredDistances.get(2),0.1);
            assertEquals(3* ScreenDistanceUpdater.LARGEST_RADIUS/3,requiredDistances.get(3),0.1);
            assertEquals((Converters.metersToMiles(new Meters(50000)).getMiles()/490 + 2)*
                    ScreenDistanceUpdater.LARGEST_RADIUS/3,requiredDistances.get(4),0.1);
        });
    }
}
