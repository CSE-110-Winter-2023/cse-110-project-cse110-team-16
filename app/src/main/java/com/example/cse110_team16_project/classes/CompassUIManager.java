package com.example.cse110_team16_project.classes;

import android.app.Activity;
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

    //private static final float SCREEN_PERCENTAGE = .475f;

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

        populateHomeIcons(homeDirectionUpdater.getHomes());

        user.getDirection().observe((LifecycleOwner) activity, direction ->
                backgroundThreadExecutor.submit(() ->
                        updateUI(direction, homeDirectionUpdater.
                                getLastKnownHomeDirectionsFromUser().getValue())
                )
        );

        homeDirectionUpdater.getLastKnownHomeDirectionsFromUser().observe((LifecycleOwner) activity,
                directions -> backgroundThreadExecutor.submit(() -> {
                    if (user.getDirection().getValue() != null) {
                        updateUI(user.getDirection().getValue(), directions);
                    }
                })
        );
    }

    //create views on the UI for each home
    public void populateHomeIcons(List<Home> homes) {
        if(homes.size() == 0) return;
        sampleHome.setText(homes.get(0).getLabel());
    }

    public void updateUI(float userDirection, List<Float> homeDirections) {
        updateCompassDirection(userDirection);
        updateHomeIconDirections(userDirection, homeDirections);
    }

    public void updateHomeIconDirections( float userDirection, List<Float> homeDirections) {
            // Set direction for sample home
            updateIconDirection(sampleHome, homeDirections.get(0) - userDirection);
    }

    //update the position of the view representing a home on the compass to the correct direction
    //params View to update, direction the home is from user in degrees from absolute north
    public void updateIconDirection(TextView tv, Float homeDirection) {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) tv.getLayoutParams();
        layoutParams.circleAngle = homeDirection;
        activity.runOnUiThread(() -> tv.setLayoutParams(layoutParams));
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
