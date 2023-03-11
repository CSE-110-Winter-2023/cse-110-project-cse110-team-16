package com.example.cse110_team16_project.classes.UI;

import android.app.Activity;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.example.cse110_team16_project.Units.Degrees;
import com.example.cse110_team16_project.Units.Radians;
import com.example.cse110_team16_project.classes.Misc.Converters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class CompassUIManager {
    private final ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> future;
    //private static final float SCREEN_PERCENTAGE = .475f;

    Activity activity;
    private final ImageView compass;

    public CompassUIManager(Activity activity, @NonNull LiveData<Radians> userDirection,
                            ImageView compass){
        this.activity = activity;
        this.compass = compass;

        userDirection.observe((LifecycleOwner) activity, direction ->
                this.future = backgroundThreadExecutor.submit(() -> {
                    updateUI(Converters.RadiansToDegrees(direction));
                    return null;
                })
        );
    }

    //given userDirection in degrees, changes compass to face correct direction
    public void updateUI(Degrees userDirection) {
        activity.runOnUiThread(() -> {
            //compass.startAnimation(ra);
            compass.setRotation((float)-userDirection.getDegrees());
            //Log.d(TAG,Float.toString(compass.getRotation()));
        });
    }

    public Future<Void> getFuture(){return future;}
}
