package com.example.cse110_team16_project.classes.UI;

import static java.security.AccessController.getContext;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Constraints;
import androidx.core.util.Pair;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cse110_team16_project.R;
import com.example.cse110_team16_project.classes.DeviceInfo.OrientationService;
import com.example.cse110_team16_project.classes.Misc.Converters;
import com.example.cse110_team16_project.classes.Units.Radians;
import com.example.cse110_team16_project.classes.Units.Degrees;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


/** Currently unused class previously meant for managing the Location icons on compass.
 * Recommend using ViewGroup instead.
 */
public class UserUIAdapter{


    private final ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> future;
    Activity activity;
    private List<String> friendLabels;
    private LiveData<List<Double>> friendDistances;
    private LiveData<List<Degrees>> friendOrientation;
    private List<TextView> friends;

    private LiveData<Radians> userOrientation;

    private  Radians userDirection;

    public UserUIAdapter(Activity activity, @NonNull LiveData<List<Double>> friendDistances,
                         @NonNull LiveData<List<Degrees>> friendOrientation, List<String> friendLabels
                         , LiveData<Radians> userOrientation){
        this.friends = new ArrayList<>();
        this.activity = activity;
        this.friendDistances = friendDistances;
        this.friendOrientation = friendOrientation;
        this.friendLabels = friendLabels;
        populateFriendLabels(friendLabels);

        userOrientation.observe((LifecycleOwner) activity,
                userOri -> this.future = backgroundThreadExecutor.submit(() -> {
                    userDirection = userOri;
                    return null;
                })
        );
        friendDistances.observe((LifecycleOwner) activity,
                    distances -> this.future = backgroundThreadExecutor.submit(() -> {
                        updateDistanceUI(distances);
                        return null;
                    })
            );

        friendOrientation.observe((LifecycleOwner) activity,
                orientation -> this.future = backgroundThreadExecutor.submit(() -> {
                    updateDirectionUI(Converters.RadiansToDegrees(userDirection), orientation);
                    return null;
                })
        );

    }


    public void populateFriendLabels(List<String> friendLabels) {
        if(friendLabels.size() == 0){
            return;
        }
        for(int i = 0 ; i < friendLabels.size() ; i++) {
            // Get the CompassActivity layout
            TextView addFriend = new TextView(activity);
            addFriend.setText(friendLabels.get(i));
            addFriend.setId(View.generateViewId());
            friends.add(addFriend);
        }
        displayFriendLabels();
    }

    public void displayFriendLabels(){
        int deviceHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        int deviceWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int halfDeviceHeight = deviceHeight/2;
        int halfDeviceWidth = deviceWidth/2;

        Log.d("DEVICEINFO", "Device height is :" + deviceHeight);
        Log.d("DEVICEINFO", "Device width is " + deviceWidth);

        ConstraintLayout parentLayout = activity.findViewById(R.id.MainLayout);
        ImageView spot = activity.findViewById(R.id.userPosition);



        for(TextView friend: friends){
            float textAngle = (float) Math.PI / 180 * 225;
            int textRadius = 300;

            spot.post(new Runnable() {
                @Override
                public void run() {
                    final int spotX = (int) spot.getX() - spot.getWidth()/2;
                    final int spotY = (int) spot.getY() - spot.getWidth()/2;

                    Log.d("DEVICEINFO", "spotX is :" + spotX);
                    Log.d("DEVICEINFO", "spotY is " + spotY);

                    int friendX = (int) spotX + (int) (Math.cos(textAngle - Math.PI/2) * textRadius);
                    int friendY = (int) spotY + (int) (Math.sin(textAngle - Math.PI/2) * textRadius);

                    friend.setHeight(300);
                    friend.setWidth(300);

                    friend.setX(friendX);
                    friend.setY(friendY);

                    parentLayout.addView(friend);
                }
            });
        }
    }

    public void updateDirectionUI(Degrees userDirection, List<Degrees> friendOrientation){
        if(friendOrientation.size() == 0){
            return;
        }
        updateIconDirections(userDirection, friends, friendOrientation);

    }

    public void updateDistanceUI(List<Double> friendDistances){
        if(friendDistances.size() == 0){
            return;
        }
        updateIconDistances(friends, friendDistances);
    }

    //update the position of the view representing a entity on the compass to the correct direction
    //params View to update, relative direction the entity is from user in degrees
    public void updateIconDirections(Degrees userDirection, List<TextView> friendViews, List<Degrees> friendOrientation) {
        for(int i = 0 ; i < friendOrientation.size() ; i++){
            friendOrientation.set(i, Degrees.subtractDegrees(friendOrientation.get(i),userDirection));
        }
        for(int i = 0 ; i < friendViews.size() ; i++) {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) friendViews.get(i).getLayoutParams();
            layoutParams.circleAngle = (float) friendOrientation.get(i).getDegrees();
            friendViews.get(i).setLayoutParams(layoutParams);
        }
    }

    public void updateIconDistances(List<TextView> friendViews, List<Double> friendDistances){
        for(int i = 0 ; i < friendViews.size() ; i++) {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) friendViews.get(i).getLayoutParams();
            layoutParams.circleRadius = friendDistances.get(i).intValue();
            friendViews.get(i).setLayoutParams(layoutParams);
        }
    }

    public void updateMapIcons(){
        var executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            updateDirectionUI(Converters.RadiansToDegrees(userOrientation.getValue()),
                    friendOrientation.getValue());
            updateDistanceUI(friendDistances.getValue());
        }, 0, 3, TimeUnit.SECONDS);

    }


}
