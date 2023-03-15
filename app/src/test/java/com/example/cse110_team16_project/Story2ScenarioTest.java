package com.example.cse110_team16_project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.rule.GrantPermissionRule;

import com.example.cse110_team16_project.Database.SCLocationDao;
import com.example.cse110_team16_project.Database.SCLocationDatabase;
import com.example.cse110_team16_project.Database.SCLocationRepository;
import com.example.cse110_team16_project.classes.CoordinateClasses.Coordinates;
import com.example.cse110_team16_project.classes.CoordinateClasses.SCLocation;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class Story2ScenarioTest {

    private final int WAIT_FOR_UPDATE_TIME = 1500;
    private SCLocationDao dao;
    private SCLocationDatabase db;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule
            .grant(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION);

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

    // Helper: find all TextViews of parent View
    List<TextView> friends = new ArrayList<>();
    public void findViews(View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    findViews(child);
                }
            }
            else if (v instanceof TextView) {
                this.friends.add((TextView)v);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void story2Scenario2() {
        // at least one UID added, check friend direction
        ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(activity -> {
            // Add UID
            SCLocationRepository repo = new SCLocationRepository(this.dao);
            SCLocation cse = new SCLocation(32.8818, -117.2335,
                    "CSE", "cse-building");
            SCLocation san = new SCLocation(32.73391790603972, -117.19278881862179,
                    "SAN", "SAN");
            repo.upsertLocal(cse);
            try {
                Thread.sleep(WAIT_FOR_UPDATE_TIME);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            assertTrue(repo.existsLocal(cse.getPublicCode()));

            try {
                Thread.sleep(15*WAIT_FOR_UPDATE_TIME);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            ConstraintLayout parent = activity.findViewById(R.id.MainLayout);
            this.findViews(parent);
//            for (TextView friend : friends) {
//                if (friend.getText() == "") {
//                    assertEquals(R.drawable.friend_triangle, friend.getTag());
//                }
//            }
        });
    }

}
