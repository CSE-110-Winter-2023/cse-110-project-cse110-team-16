package com.example.cse110_team16_project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class Story4ScenarioTest {

    // Test for Story 4 Scenario 1
    @Test
    public void story4Scenario1(){
        var scenario = ActivityScenario.launch(AddNameActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);

        scenario.onActivity(activity -> {
            String testName = "Dave";
            EditText editText = activity.findViewById(R.id.YourNameField);
            editText.setText(testName);

            Button submit_btn = activity.findViewById(R.id.submitNameBtn);
            submit_btn.performClick();

            var preferences = RuntimeEnvironment.getApplication()
                                                .getSharedPreferences("user_info", Context.MODE_PRIVATE);

            String SP_name = preferences.getString("label", "");
            String public_code = preferences.getString("public_code", "");
            String private_code = preferences.getString("private_code", "");
            assertEquals(testName, SP_name);
            assertNotEquals("", public_code);
            assertNotEquals("", private_code);
        });
    }
}
