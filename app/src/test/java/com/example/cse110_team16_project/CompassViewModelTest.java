package com.example.cse110_team16_project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.rule.GrantPermissionRule;

import com.example.cse110_team16_project.Units.Meters;
import com.example.cse110_team16_project.classes.CompassViewModel;
import com.example.cse110_team16_project.classes.Misc.Converters;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class CompassViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule
            .grant(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Test
    public void testFindScreenDistance(){
        ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(CompassActivity.class);

        Application application = RuntimeEnvironment.getApplication();
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onActivity(activity ->
        {
            List<Meters> distances = new ArrayList<>();
            distances.add(new Meters(300)); //.186411 miles
            distances.add(new Meters(2000));  //1.24274 miles
            distances.add(new Meters(70)); // 0.043496
            distances.add(new Meters(5000000)); // 3106.855961
            distances.add(new Meters(50000)); // 31.06856

            CompassViewModel viewModel = new CompassViewModel(application);
            List<Double> requiredDistances = viewModel.findScreenDistance(distances,3);
            assertEquals(Converters.metersToMiles(new Meters(300)).getMiles()*
                    CompassViewModel.SECTOR_RADIUS,requiredDistances.get(0),0.1);
            assertEquals((Converters.metersToMiles(new Meters(2000)).getMiles()/9 + 1)*
                    CompassViewModel.SECTOR_RADIUS,requiredDistances.get(1),0.1);
            assertEquals((Converters.metersToMiles(new Meters(70)).getMiles()/1 + 0)*
                    CompassViewModel.SECTOR_RADIUS,requiredDistances.get(2),0.1);
            assertEquals(3*CompassViewModel.SECTOR_RADIUS,requiredDistances.get(3),0.1);
            assertEquals((Converters.metersToMiles(new Meters(50000)).getMiles()/490 + 2)*
                    CompassViewModel.SECTOR_RADIUS,requiredDistances.get(4),0.1);
        });
    }
}
