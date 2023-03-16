package com.example.cse110_team16_project;

import static org.junit.Assert.assertEquals;

import android.app.Activity;
import android.opengl.Visibility;
import android.view.View;
import android.widget.ImageView;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;

import com.example.cse110_team16_project.classes.Updaters.ScreenDistanceUpdater;
import com.example.cse110_team16_project.classes.ZoomManager;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class ZoomManagerTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void testZoomLevel() {
        ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(CompassActivity.class);

        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onActivity(activity -> {
            ScreenDistanceUpdater sdu = new ScreenDistanceUpdater(activity, 4);
            ZoomManager zoomManager = new ZoomManager(activity, sdu);

            // Start at default zoom
            assertEquals(2, zoomManager.getZoomLevel());
            assertEquals(2, zoomManager.readZoomLevel());

            // Zoom in twice
            zoomManager.zoomIn();
            assertEquals(1, zoomManager.getZoomLevel());
            assertEquals(1, zoomManager.readZoomLevel());

            // Zoom out once
            zoomManager.zoomOut();
            assertEquals(2, zoomManager.getZoomLevel());
            assertEquals(2, zoomManager.readZoomLevel());
        });
    }

    @Test
    public void testRingVisibility() {
        ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(CompassActivity.class);

        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onActivity(activity -> {
            ScreenDistanceUpdater sdu = new ScreenDistanceUpdater(activity, 4);
            ZoomManager zoomManager = new ZoomManager(activity, sdu);
            ImageView ring1 = activity.findViewById(R.id.zeroToOneMilesRing);
            ImageView ring2 = activity.findViewById(R.id.oneTotenMilesRing);
            ImageView ring3 = activity.findViewById(R.id.tenTo500MilesRing);
            ImageView ring4 = activity.findViewById(R.id.outerRing);
            ImageView ringsArr[] = {ring1, ring2, ring3, ring4};
            List<ImageView> rings = Arrays.asList(ringsArr);

            // Start at default zoom
            for (int i = 0; i < ringsArr.length; ++i) {
                if (i < 2) assertEquals(View.INVISIBLE, ringsArr[i].getVisibility());
                else assertEquals(View.VISIBLE, ringsArr[i].getVisibility());
            }

            // Zoom in one time
            zoomManager.zoomIn();

            for (ImageView r : rings) {
                if (r != ring4) assertEquals(View.INVISIBLE, r.getVisibility());
                else assertEquals(View.VISIBLE, r.getVisibility());
            }

            // Zoom out once
            zoomManager.zoomOut();
            for (ImageView r : rings) {
                if (r == ring1 || r == ring2) assertEquals(View.INVISIBLE, r.getVisibility());
                else assertEquals(View.VISIBLE, r.getVisibility());
            }
        });
    }
}
