package com.example.cse110_team16_project;

import static com.example.cse110_team16_project.classes.Updaters.ScreenDistanceUpdater.LARGEST_RADIUS;
import static org.junit.Assert.assertEquals;
import static org.robolectric.annotation.LooperMode.Mode.PAUSED;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.rule.GrantPermissionRule;

import com.example.cse110_team16_project.Database.SCLocationDao;
import com.example.cse110_team16_project.Database.SCLocationDatabase;
import com.example.cse110_team16_project.classes.CoordinateClasses.SCLocation;
import com.example.cse110_team16_project.classes.Misc.Converters;
import com.example.cse110_team16_project.classes.UI.UserIconManager;
import com.example.cse110_team16_project.classes.Units.Degrees;
import com.example.cse110_team16_project.classes.Units.Meters;
import com.example.cse110_team16_project.classes.Units.Radians;
import com.example.cse110_team16_project.classes.Updaters.ScreenDistanceUpdater;
import com.example.cse110_team16_project.classes.ZoomManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.LooperMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class Story9ScenarioTest {
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

    // Helper: find all TextViews of parent View0
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
    @LooperMode(PAUSED)
    public void story9Scenario() {
        ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(CompassActivity.class);

        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onActivity(activity -> {
            int zoneSetting = 2;
            ScreenDistanceUpdater sdu = new ScreenDistanceUpdater(activity, 2);
            ZoomManager zoomManager = new ZoomManager(activity, sdu);
            ImageView ring1 = activity.findViewById(R.id.zeroToOneMilesRing);
            ImageView ring2 = activity.findViewById(R.id.oneTotenMilesRing);
            ImageView ring3 = activity.findViewById(R.id.tenTo500MilesRing);
            ImageView ring4 = activity.findViewById(R.id.outerRing);
            ImageView ringsArr[] = {ring1, ring2, ring3, ring4};
            List<ImageView> rings = Arrays.asList(ringsArr);

            List<Meters> meters = new ArrayList<Meters>();
            meters.add(new Meters(8000));

            List<Double> screenDists = sdu.findScreenDistance(meters);

            // Start at default zoom
            for (int i = 0; i < ringsArr.length; ++i) {
                // Check ring visibility
                if (i < 2) assertEquals(View.INVISIBLE, ringsArr[i].getVisibility());
                else assertEquals(View.VISIBLE, ringsArr[i].getVisibility());
            }

            for(int i = 0; i < screenDists.size(); ++i){
                Double expectedDist = ((Converters.metersToMiles(meters.get(i)).getMiles() / (10-1)) + 2-1)* LARGEST_RADIUS/zoneSetting;
                assertEquals(expectedDist, screenDists.get(i), 1);
            }

            // Zoom in one time
            zoomManager.zoomIn();
            --zoneSetting;

            for (ImageView r : rings) {
                if (r != ring4) assertEquals(View.INVISIBLE, r.getVisibility());
                else assertEquals(View.VISIBLE, r.getVisibility());
            }

            screenDists = sdu.findScreenDistance(meters);
            for(int i = 0; i < screenDists.size(); ++i){
                Double expectedDist = LARGEST_RADIUS;
                assertEquals(expectedDist, screenDists.get(i), 1);
            }
        });

    }
}
