package com.example.cse110_team16_project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
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
import com.example.cse110_team16_project.classes.UI.IconTruncater;
import com.example.cse110_team16_project.classes.UI.UserIconManager;
import com.example.cse110_team16_project.classes.Units.Degrees;
import com.example.cse110_team16_project.classes.Units.Radians;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class IconTruncaterTest {
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

    /*
    If two friends are in the exact same location, then both should be pushed up and down.
     */
    @Test
    public void testTruncater(){
        String verylong1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String verylong2 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1";
        List<String> labels = new ArrayList<>();
            labels.add(verylong1);
            labels.add(verylong2);

        List<Double> fD = new ArrayList<>();
            fD.add(100.0);
            fD.add(100.0);
        List<Degrees> fO = new ArrayList<>();
            fO.add(new Degrees(-45));
            fO.add(new Degrees(45));
;

        ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(CompassActivity.class);

            scenario.moveToState(Lifecycle.State.RESUMED);
            scenario.onActivity(activity -> {
                List<TextView> views = new ArrayList<>();
                TextView view = new TextView(activity);
                view.setText(verylong1);
                view.setTextColor(Color.BLUE);
                view.setMaxLines(1);
                view.setEllipsize(TextUtils.TruncateAt.END);
                view.setId(View.generateViewId());
                views.add(view);
                view = new TextView(activity);
                view.setText(verylong2);
                view.setTextColor(Color.BLUE);
                view.setMaxLines(1);
                view.setEllipsize(TextUtils.TruncateAt.END);
                view.setId(View.generateViewId());
                views.add(view);
            IconTruncater iconTruncater = new IconTruncater(fO, fD, views);
            int width1 = iconTruncater.getFinalWidth().get(0);
            iconTruncater.truncateIcons();
            assertTrue(iconTruncater.getFinalWidth().get(0) < width1);
        });

    }
}
