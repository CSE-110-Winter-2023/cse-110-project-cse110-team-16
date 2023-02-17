package com.example.cse110_team16_project;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class AddHomeLocTest extends AppCompatActivity {
    @Test
    public void testSaveProfile() {
        ActivityScenario<AddHomeLocations> scenario = ActivityScenario.launch(AddHomeLocations.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);

        scenario.onActivity(activity -> {
            EditText yourFamHome = activity.findViewById(R.id.yourFamHomeField);
            yourFamHome.setText("(12.1231323, 67.8903457)");

            System.out.println(yourFamHome.getText().toString());

            Button submit_btn = activity.findViewById(R.id.submitBtn);
            submit_btn.performClick();

            SharedPreferences preferences =
                    RuntimeEnvironment.getApplication().getSharedPreferences(
                            "HomeLoc", Context.MODE_PRIVATE);

            float yourFamX = preferences.getFloat("yourFamX", 0.0F);
            float yourFamY = preferences.getFloat("yourFamY", 0.0F);

            System.out.println(yourFamX + "");
            System.out.println(yourFamY + "");

            assertEquals(12.1231323F, yourFamX, 0.0);
            assertEquals(67.8903457F, yourFamY, 0.0);
        });
    }

    @Test
    public void testStoreCoords() {
        String coords = "(12.34567, 98.76543)";

        float[] output = AddHomeLocations.storeCoords(coords);

        assertEquals(output[0], 12.34567F, 0.0);
        assertEquals(output[1], 98.76543F, 0.0);
    }
}