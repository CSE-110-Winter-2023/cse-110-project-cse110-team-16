package com.example.cse110_team16_project;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.location.Location;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;
import androidx.test.rule.GrantPermissionRule;

import com.example.cse110_team16_project.classes.GPSstatus;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class Story7ScenarioTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule
            .grant(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION);
    @Test
    public void story7Scenario1() {
        // on when enter app
        // turn off gps
        // green --> red --> green
        ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onActivity(activity -> {
            Context context = activity.getApplicationContext();
            TextView gpsLight = activity.findViewById(R.id.gpsLight);
//            assertEquals(ResourcesCompat.getDrawable(context.getResources(), R.drawable.gps_green, null).getConstantState(),
//                    gpsLight.getBackground().getConstantState());
            try {
                MutableLiveData<Location> mockLiveLoc = new MutableLiveData<>();
                Location mockLoc = new Location("dummy provider");
                mockLoc.setLatitude(100);
                mockLoc.setLongitude(-100);
                mockLiveLoc.setValue(mockLoc);
                GPSstatus mockGPSstatus = new GPSstatus(mockLiveLoc, activity.findViewById(R.id.gpsLight),
                        activity.findViewById(R.id.gpsText));
                activity.setGpsstatus(mockGPSstatus);
                System.out.println(activity.getDeviceTracker().getLocation().getValue().toString());
                assertEquals(R.drawable.gps_green, gpsLight.getTag());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
