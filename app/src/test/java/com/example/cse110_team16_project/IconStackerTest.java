package com.example.cse110_team16_project;

import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.rule.GrantPermissionRule;

import com.example.cse110_team16_project.Database.SCLocationDao;
import com.example.cse110_team16_project.Database.SCLocationDatabase;
import com.example.cse110_team16_project.classes.UI.IconStacker;
import com.example.cse110_team16_project.classes.UI.IconTruncater;
import com.example.cse110_team16_project.classes.Units.Degrees;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class IconStackerTest {
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
    public void testStacker(){
        String str1 = "abc";
        String str2 = "def";
        List<String> labels = new ArrayList<>();
        labels.add(str1);
        labels.add(str2);

        List<Double> fD = new ArrayList<>();
        fD.add(100.0);
        fD.add(100.0);
        List<Degrees> fO = new ArrayList<>();
        fO.add(new Degrees(0));
        fO.add(new Degrees(0));

        Degrees userDirection = new Degrees(0);

        ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(CompassActivity.class);

        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onActivity(activity -> {
            List<TextView> views = new ArrayList<>();
            TextView view = new TextView(activity);
            view.setText(str1);
            view.setTextColor(Color.BLUE);
            view.setMaxLines(1);
            view.setEllipsize(TextUtils.TruncateAt.END);
            view.setId(View.generateViewId());
            views.add(view);
            view = new TextView(activity);
            view.setText(str2);
            view.setTextColor(Color.BLUE);
            view.setMaxLines(1);
            view.setEllipsize(TextUtils.TruncateAt.END);
            view.setId(View.generateViewId());
            views.add(view);
            IconStacker iconStacker = new IconStacker(userDirection, fO, fD);
            iconStacker.adjustIcons();
            List<Double> adjustedRadii = iconStacker.getAdjustedRadius();
            assertTrue((adjustedRadii.get(0) > 100 || adjustedRadii.get(1) > 100));
        });

    }
}
