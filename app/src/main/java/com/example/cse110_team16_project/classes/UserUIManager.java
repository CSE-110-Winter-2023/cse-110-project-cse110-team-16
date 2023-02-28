package com.example.cse110_team16_project.classes;

import android.app.Activity;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

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
    private List<TextView> friendLabels;
    private final int[] defaultColors = {R.color.black,
            R.color.kashmir_green, R.color.midnight_blue};
    //Initial colors of the text/icons for the first three users

    public UserUIManager(Activity activity, LiveData<Degrees> userDirection, @NonNull LiveData<List<Degrees>> lastKnownHomeDirectionsFromUser){
        this.activity = activity;

        lastKnownHomeDirectionsFromUser.observe((LifecycleOwner) activity,
                directions -> this.future = backgroundThreadExecutor.submit(() -> {
                    if (userDirection.getValue() != null) {
                        updateUI(userDirection.getValue(), directions);
                    }
                    return null;
                })
        );
    }


    public void updateUI( Degrees userDirection, List<Degrees> friendDirections) {
    }

    //update the position of the view representing a friend on the compass to the correct direction
    //params View to update, relative direction the friend is from user in degrees
    public void updateIconDirection(TextView tv, Degrees friendDirection) {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) tv.getLayoutParams();
        layoutParams.circleAngle = (float) friendDirection.getDegrees();
        activity.runOnUiThread(() -> tv.setLayoutParams(layoutParams));
    }

    public void updateIconLabel(TextView tv, String label){
        tv.setText(label);
    }
}
