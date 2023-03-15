package com.example.cse110_team16_project.classes.UI;

import static java.security.AccessController.getContext;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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

    private final int halfDeviceHeight = Resources.getSystem().getDisplayMetrics().heightPixels/2;
    private final int halfDeviceWidth = Resources.getSystem().getDisplayMetrics().widthPixels/2;
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


    public void displayFriendLabel(int radius, float angle, int height, int width, TextView tv, ConstraintLayout parent){
        ImageView spot = activity.findViewById(R.id.userPosition);
        float finalAngle = angle - (float)Math.PI/2;
        spot.post(new Runnable() {
            @Override
            public void run() {
                final int spotX = (int) spot.getX() - spot.getWidth()/2;
                final int spotY = (int) spot.getY() - spot.getWidth()/2;

                int friendX = (int) spotX + (int) (Math.cos(finalAngle) * radius);
                int friendY = (int) spotY + (int) (Math.sin(finalAngle) * radius);

                tv.setHeight(height);
                tv.setWidth(width);

                tv.setX(friendX);
                tv.setY(friendY);
                Context context = tv.getContext();
                Drawable top = context.getResources().getDrawable(R.drawable.friend_triangle);
                tv.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);

                if(tv.getParent() != null) {
                    ((ViewGroup)tv.getParent()).removeView(tv);
                }

                parent.addView(tv);
            }
        });
    }

    public void displayFriendLabels(){
        ConstraintLayout parentLayout = activity.findViewById(R.id.MainLayout);
        var executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            Log.d("UIManager", Boolean.toString(this.friendDistances.getValue() != null));
            Log.d("FriendSize", Integer.toString(this.friends.size()));
            List<Double> retrievedDistList = this.friendDistances.getValue();

            for (int i = 0; i < this.friends.size(); i++) {
                TextView friend = this.friends.get(i);

                  if(retrievedDistList == null) return;
                    Double dist = retrievedDistList.get(i); //possible null here

                    if (friendOrientation != null) {
                        Degrees angle = this.friendOrientation.getValue().get(i);
                        displayFriendLabel(dist.intValue(), (float) angle.getDegrees(), 200, 250, friend, parentLayout);
                    }
            }
        }, 0, 1, TimeUnit.SECONDS);


//        for(TextView friend: friends){
//            float angleDegree = 225;
//            float textAngle = (float) Math.PI / 180 * angleDegree;
//            int textRadius = 300;
//
//            displayFriendLabel(textRadius, textAngle, 100, 250, friend, parentLayout);
//        }
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
        }, 0, 100, TimeUnit.MILLISECONDS);

    }

    public void destroyTextViews(){
        for(TextView tv: friends){
            ((ViewGroup)tv.getParent()).removeView(tv);
        }
    }


}
