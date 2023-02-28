package com.example.cse110_team16_project.classes;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;

import com.example.cse110_team16_project.R;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UserUIManager {
    private final ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> future;
    //private static final float SCREEN_PERCENTAGE = .475f;

    Activity activity;
    private List<TextView> homeLabels;
    private final int[] defaultColors = {R.color.black,
            R.color.kashmir_green, R.color.midnight_blue};
    //Initial colors of the text/icons for the first three homes
    private final TextView sampleHome;

    public UserUIManager(Activity activity, @NonNull User user, @NonNull HomeDirectionUpdater homeDirectionUpdater, TextView sampleHome){
        this.activity = activity;
        this.sampleHome = sampleHome;
        populateHomeIcons(homeDirectionUpdater.getHomes());
        user.getDirection().observe((LifecycleOwner) activity, direction ->
                this.future = backgroundThreadExecutor.submit(() -> {
                    updateUI(direction, homeDirectionUpdater.
                            getLastKnownHomeDirectionsFromUser().getValue());
                    return null;
                })
        );

        homeDirectionUpdater.getLastKnownHomeDirectionsFromUser().observe((LifecycleOwner) activity,
                directions -> this.future = backgroundThreadExecutor.submit(() -> {
                    if (user.getDirection().getValue() != null) {
                        updateUI(user.getDirection().getValue(), directions);
                    }
                    return null;
                })
        );
    }

    public void populateHomeIcons(List<Home> homes) {
        if(homes.size() == 0) return;
        sampleHome.setText(homes.get(0).getLabel());
    }

    public void updateUI( Degrees userDirection, List<Degrees> homeDirections) {
        // Set direction for sample home
        if(homeDirections.size() == 0) return;
        updateIconDirection(sampleHome, Degrees.subtractDegrees(homeDirections.get(0),userDirection));
    }

    //update the position of the view representing a home on the compass to the correct direction
    //params View to update, direction the home is from user in degrees from absolute north
    public void updateIconDirection(TextView tv, Degrees homeDirection) {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) tv.getLayoutParams();
        layoutParams.circleAngle = (float) homeDirection.getDegrees();
        activity.runOnUiThread(() -> tv.setLayoutParams(layoutParams));
        // Deprecated label setting
        //tv.setText("Best Friend's House");
    }
    public void updateIconLabel(TextView tv, String label){
        tv.setText(label);
    }
}
