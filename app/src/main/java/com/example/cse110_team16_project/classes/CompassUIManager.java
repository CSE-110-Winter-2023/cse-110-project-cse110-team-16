package com.example.cse110_team16_project.classes;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;

import com.example.cse110_team16_project.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CompassUIManager {
    private final ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();

    private static final float SCREEN_PERCENTAGE = .475f;
    Activity activity;
    private List<TextView> homeLabels;
    private final int[] defaultColors = {R.color.black,
            R.color.kashmir_green, R.color.midnight_blue};
    //Initial colors of the text/icons for the first three homes
    private final ImageView compass;
    private final TextView sampleHome;

    public CompassUIManager(Activity activity, @NonNull User user, @NonNull HomeDirectionUpdater homeDirectionUpdater,
                            ImageView compass, TextView sampleHome){
        this.activity = activity;
        this.compass = compass;
        this.sampleHome = sampleHome;

        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int dpRadius = (int) (dpWidth / SCREEN_PERCENTAGE);
        //potentially use to make app work with different screen sizes

        populateHomeIcons(homeDirectionUpdater.getHomes());

        user.getDirection().observe((LifecycleOwner) activity, direction ->
                backgroundThreadExecutor.submit(() ->
                        updateUI(direction, homeDirectionUpdater.
                                getLastKnownHomeDirectionsFromUser())
                )
        );

    }

    //create views on the UI for each home
    public void populateHomeIcons(List<Home> homes) {

        homeLabels = new ArrayList<>(homes.size());
        for (int i = 0; i < homes.size(); i++) {
            TextView tv = new TextView(activity);
            //add stuff here
            homeLabels.add(tv);
        }
    }

    //update the position of the view representing a home on the compass to the correct direction

    //params View to update, direction the home is from user in degrees from absolute north
    public void updateIconDirection(TextView tv, Float homeDirection) {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) tv.getLayoutParams();
        layoutParams.circleAngle = homeDirection;
        activity.runOnUiThread(() -> tv.setLayoutParams(layoutParams));
    }


    public void updateUI(float userDirection, List<Float> homeDirections) {
        //if(Math.abs(userDirection - compass.getRotation()) < .15f) return;

        updateCompassDirection(userDirection);
        updateHomeIconDirections(homeDirections, userDirection);
    }

    public void updateHomeIconDirections(List<Float> homeDirections, float userDirection) {
//        for (int i = 0; i < homeLabels.size(); i++) {
            // TODO: fake coordinates, should use robolectric test
            Coordinates homePos = new Coordinates(32.734648946916835, -117.19090054085841);
            Coordinates userPos = new Coordinates(32.8806731315563, -117.23402032381517);
            final float homeDirection = userPos.bearingTo(homePos);

            // final float homeDirection = homeDirections.get(i);

                // Set direction for sample home
                updateIconDirection(sampleHome, homeDirection - userDirection);

//        }
    }

    //given userDirection in degrees, changes compass to face correct direction
    public void updateCompassDirection(float userDirection) {

        activity.runOnUiThread(() -> {
            //compass.startAnimation(ra);
            compass.setRotation(-userDirection);
            //Log.d(TAG,Float.toString(compass.getRotation()));
        });


    }
}
