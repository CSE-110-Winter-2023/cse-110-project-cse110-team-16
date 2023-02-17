package com.example.cse110_team16_project;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.util.Log;
//import android.widget.Button;
//import android.widget.EditText;
//import static org.junit.Assert.assertEquals;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.lifecycle.Lifecycle;
//import androidx.test.core.app.ActivityScenario;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.robolectric.RobolectricTestRunner;
//
//@RunWith(RobolectricTestRunner.class)
//public class AddHomeLocTest{
//
//    @Test
//    public void test_storage_floats() extends AppCompatActivity{
//        ActivityScenario scenario = ActivityScenario.launch(MainActivity.class);
//        scenario.moveToState(Lifecycle.State.CREATED);
//        scenario.moveToState(Lifecycle.State.STARTED);
//
//        scenario.onActivity(activity -> {
////            EditText yourFamHome = activity.findViewById(R.id.yourFamHomeField);
////            yourFamHome.setText("(12.1231323, 67.8903457)");
////
////            Button submit_btn = activity.findViewById(R.id.submitBtn);
////            submit_btn.performClick();
////
////            SharedPreferences preferences = getSharedPreferences("HomeLoc", Context.MODE_PRIVATE);
////
////            float yourFamX = preferences.getFloat("yourFamX", 0.0F);
////            float yourFamY = preferences.getFloat("yourFamY", 0.0F);
////            assertEquals(12.1231323F, yourFamX, 0.0);
////            assertEquals(67.8903457F, yourFamY, 0.0);
//            assertEquals(1,1);
//        });
//    }
//}

import static org.junit.Assert.assertEquals;

import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class AddHomeLocTest {
    @Test
    public void test_one_plus_one_equals_two() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);

        scenario.onActivity(activity -> {

            assertEquals(2, 2);
        });
    }
}