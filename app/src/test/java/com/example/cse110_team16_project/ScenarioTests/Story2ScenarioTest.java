package com.example.cse110_team16_project.ScenarioTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.annotation.LooperMode.Mode.PAUSED;

import android.content.Context;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.rule.GrantPermissionRule;

import com.example.cse110_team16_project.CompassActivity;
import com.example.cse110_team16_project.Database.SCLocationDao;
import com.example.cse110_team16_project.Database.SCLocationDatabase;
import com.example.cse110_team16_project.Database.SCLocationRepository;
import com.example.cse110_team16_project.R;
import com.example.cse110_team16_project.classes.CoordinateClasses.Coordinates;
import com.example.cse110_team16_project.classes.CoordinateClasses.SCLocation;
import com.example.cse110_team16_project.classes.UI.UserIconManager;
import com.example.cse110_team16_project.classes.Units.Degrees;
import com.example.cse110_team16_project.classes.Units.Radians;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.LooperMode;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
    public void story2Scenario1() {
        // at least one UID added, check friend direction
        // Add UID
        SCLocation cse = new SCLocation(32.8818, -117.2335,
                "CSE", "cse-building");
        SCLocation san = new SCLocation(32.73391790603972, -117.19278881862179,
                "SAN", "SAN"); //COORDINATES NOT USED FOR DETERMINING UI

        List<String> labels = new ArrayList<>();
        labels.add("CSE");

        List<Double> fD = new ArrayList<>();
        fD.add(470.0);
        List<Degrees> fO = new ArrayList<>();
        fO.add(new Degrees(0));

        MutableLiveData<List<Double>> friendDistances = new MutableLiveData<>(fD);
        MutableLiveData<List<Degrees>> friendOrientation = new MutableLiveData<>(fO);
        MutableLiveData<Radians> userOrientation = new MutableLiveData<>(new Radians(Math.PI));


        ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(CompassActivity.class);

        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onActivity(activity -> {

            UserIconManager iconManager = new UserIconManager(activity, friendDistances, friendOrientation, userOrientation);
            iconManager.onFriendsChanged(labels);
            iconManager.updateUI(new Degrees(180),fO,fD);
            ConstraintLayout parent = activity.findViewById(R.id.MainLayout);
            TextView icon =  iconManager.getTextViews().get(0);
            assertEquals("CSE", icon.getText());
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) icon.getLayoutParams();
            float angle = layoutParams.circleAngle;
            int distance = layoutParams.circleRadius;
            assertEquals(180, Math.abs(layoutParams.circleAngle), 1);
        });

    }

}
