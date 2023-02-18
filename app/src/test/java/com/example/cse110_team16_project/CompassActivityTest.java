package com.example.cse110_team16_project;

import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.annotation.LooperMode.Mode.PAUSED;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.widget.ImageView;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.GrantPermissionRule;

import com.example.cse110_team16_project.classes.Coordinates;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.LooperMode;
import org.robolectric.shadows.ShadowApplication;

import java.util.concurrent.TimeUnit;

@RunWith(RobolectricTestRunner.class)
public class CompassActivityTest {


    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule
            .grant(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION);


    /*
    Given that the user is on the “compass” screen of the app
    And the user has inputted the coordinates of his parents’ home
    And the app can correctly read the GPS location on phone
    And the app shows the direction of his parents' home on the “compass” screen correctly
    When the user rotates his body to the right
    Then the three coordinates on the compass screen will move left to indicate that the direction towards those coordinates veers more left.
    */
    @Test
    @LooperMode(PAUSED)
    public void Story4Scenario1Test() {
        SharedPreferences labelPreferences = RuntimeEnvironment.getApplication().
                getSharedPreferences("FamHomeLabel", Context.MODE_PRIVATE);
        labelPreferences.edit().putString("famLabel", "Parents' Home").commit();

        SharedPreferences locationPreferences = RuntimeEnvironment.getApplication().
                getSharedPreferences("HomeLoc", Context.MODE_PRIVATE);
        locationPreferences.edit().putFloat("yourFamX", 32.13164f).commit();
        locationPreferences.edit().putFloat("yourFamY", 22.13144f).commit();

        ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onActivity(activity ->{
            assertNotNull(activity.getHomes());
            assertEquals(1,activity.getHomes().size());
            assertEquals("Parents' Home", activity.getHomes().get(0).getLabel());
            assertEquals(32.13164, activity.getHomes().get(0).getCoordinates().getLatitude(),0.001);
            assertEquals(22.13144, activity.getHomes().get(0).getCoordinates().getLongitude(),0.001);

            activity.getUser().setCoordinates(new Coordinates(33.1643,22.0011));
            assertEquals(new Coordinates(33.1643,22.0011),activity.getUser().getCoordinates().getValue());
            ImageView compassView = activity.findViewById(R.id.compassRing);
            assertEquals(0.0f,compassView.getRotation(),0.1f);
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)
                    activity.findViewById(R.id.sampleHome).getLayoutParams();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            assertEquals(174,layoutParams.circleAngle,1f);
            activity.getUser().setDirection(90);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            shadowOf(Looper.getMainLooper()).idle();
            assertEquals(174-90,layoutParams.circleAngle,1f);
            assertEquals(-90.0f,compassView.getRotation(),0.1f);
        });
    }
}
