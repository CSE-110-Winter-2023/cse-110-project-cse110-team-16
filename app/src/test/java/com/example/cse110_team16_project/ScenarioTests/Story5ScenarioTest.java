package com.example.cse110_team16_project.ScenarioTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;

import com.example.cse110_team16_project.ListActivity;
import com.example.cse110_team16_project.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.UUID;

@RunWith(RobolectricTestRunner.class)
public class Story5ScenarioTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    // Test for Story 5 Scenario 1
    @Test
    public void testInvisibleToVisible(){
        String public_code = UUID.randomUUID().toString();
        var preferences = RuntimeEnvironment.getApplication()
                .getSharedPreferences("user_info", Context.MODE_PRIVATE);
        var editor = preferences.edit();
        editor.putString("public_code", public_code);
        editor.apply();

        var scenario = ActivityScenario.launch(ListActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(activity -> {
            Button UID_Btn = activity.findViewById(R.id.show_uid_btn);
            TextView UID_display = activity.findViewById(R.id.uid_text);
            UID_display.setVisibility(View.INVISIBLE);
            assertEquals(View.INVISIBLE, UID_display.getVisibility());

            UID_Btn.performClick();
            assertEquals(View.VISIBLE, UID_display.getVisibility());
            assertEquals(public_code, UID_display.getText().toString());
        });
    }

    // Test for Story 5 Scenario 2
    @Test
    public void testVisibleToInvisible(){
        String public_code = UUID.randomUUID().toString();
        var preferences = RuntimeEnvironment.getApplication()
                .getSharedPreferences("user_info", Context.MODE_PRIVATE);
        var editor = preferences.edit();
        editor.putString("public_code", public_code);
        editor.apply();

        var scenario = ActivityScenario.launch(ListActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(activity -> {
            Button UID_Btn = activity.findViewById(R.id.show_uid_btn);
            TextView UID_display = activity.findViewById(R.id.uid_text);
            UID_display.setVisibility(View.VISIBLE);
            assertEquals(View.VISIBLE, UID_display.getVisibility());
            assertEquals(public_code, UID_display.getText().toString());

            UID_Btn.performClick();
            assertEquals(View.INVISIBLE, UID_display.getVisibility());
        });
    }
}
