package com.example.cse110_team16_project;

import static org.junit.Assert.*;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowApplication;

@RunWith(RobolectricTestRunner.class)
public class CompassActivityTest {


    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule
            .grant(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Test
    public void IntegrationTest() {
        SharedPreferences labelPreferences = RuntimeEnvironment.getApplication().
                getSharedPreferences("FamHomeLabel", Context.MODE_PRIVATE);
        labelPreferences.edit().putString("famLabel", "Parents' Home").commit();

        SharedPreferences locationPreferences = RuntimeEnvironment.getApplication().
                getSharedPreferences("HomeLoc", Context.MODE_PRIVATE);
        locationPreferences.edit().putFloat("yourFamX", 32.13164f).commit();
        locationPreferences.edit().putFloat("yourFamY", 22.13144f).commit();
        assertEquals(32.13164f,locationPreferences.getFloat("yourFamX",-1f),.0001f);

        ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.onActivity(activity ->{
            assertNotNull(activity.getHomes());
            assertEquals(1,activity.getHomes().size());
            assertEquals("Parents' Home", activity.getHomes().get(0).getLabel());
            assertEquals(32.13164, activity.getHomes().get(0).getCoordinates().getLatitude(),0.001);
            assertEquals(22.13144, activity.getHomes().get(0).getCoordinates().getLongitude(),0.001);
        });
    }
}
