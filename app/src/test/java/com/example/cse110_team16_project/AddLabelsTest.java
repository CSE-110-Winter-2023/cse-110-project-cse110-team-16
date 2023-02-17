package com.example.cse110_team16_project;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class AddLabelsTest extends AppCompatActivity {
    @Test
    public void testSaveProfile() {
        ActivityScenario<AddLabels> scenario = ActivityScenario.launch(AddLabels.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);

        scenario.onActivity(activity -> {
            EditText famLabelText = activity.findViewById(R.id.famHomeLabelField);
            famLabelText.setText("Family's Home");

            Button submit_btn = activity.findViewById(R.id.submitBtn);
            submit_btn.performClick();

            SharedPreferences preferences =
                    RuntimeEnvironment.getApplication().getSharedPreferences(
                            "FamHomeLabel", Context.MODE_PRIVATE);

            String famLabel = preferences.getString("famLabel", "");

            assertEquals("Family's Home", famLabel);
        });
    }

    @Test
    public void testSaveProfileWithNoInput() {
        ActivityScenario<AddLabels> scenario = ActivityScenario.launch(AddLabels.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);

        scenario.onActivity(activity -> {
            EditText famLabelText = activity.findViewById(R.id.famHomeLabelField);
            famLabelText.setText("");

            Button submit_btn = activity.findViewById(R.id.submitBtn);
            submit_btn.performClick();

            SharedPreferences preferences =
                    RuntimeEnvironment.getApplication().getSharedPreferences(
                            "FamHomeLabel", Context.MODE_PRIVATE);

            String famLabel = preferences.getString("famLabel", "");

            assertEquals("", famLabel);
        });
    }

}