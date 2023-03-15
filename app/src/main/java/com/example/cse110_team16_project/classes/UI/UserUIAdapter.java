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
import android.view.ViewParent;
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


    public UserUIAdapter(Activity activity, @NonNull LiveData<List<Double>> friendDistances,
                         @NonNull LiveData<List<Degrees>> friendOrientation
                         , LiveData<Radians> userOrientation){
        this.friends = new ArrayList<>();
        this.activity = activity;
        this.friendDistances = friendDistances;
        this.friendOrientation = friendOrientation;

        friendDistances.observe((LifecycleOwner) activity,
                    distances -> this.future = backgroundThreadExecutor.submit(() -> {
                        updateUI(Converters.RadiansToDegrees(userOrientation.getValue()),friendOrientation.getValue(),distances);
                        return null;
                    })
            );

        userOrientation.observe((LifecycleOwner) activity,
                orientation -> this.future = backgroundThreadExecutor.submit(() -> {
                    updateUI(Converters.RadiansToDegrees(orientation),friendOrientation.getValue(),friendDistances.getValue());
                    return null;
                })
        );
    }


    public void onFriendsChanged(List<String> newFriendLabels) {
        destroyTextViews();
        friends.clear();
        this.friendLabels = newFriendLabels;
        if(friendLabels.size() == 0){
            return;
        }
        for(int i = 0 ; i < friendLabels.size() ; i++) {
            // Get the CompassActivity layout
            TextView addFriend = new TextView(activity);
            addFriend.setText(friendLabels.get(i));
            addFriend.setId(View.generateViewId());
            addFriend.setVisibility(View.INVISIBLE);
            friends.add(addFriend);
        }
        displayFriendLabels();
    }


    public void displayFriendLabel(int radius, float angle, int height, int width, TextView tv, ConstraintLayout parent){
        ImageView spot = activity.findViewById(R.id.userPosition);
        float finalAngle = angle - (float)Math.PI/2;
        spot.post(() -> {


            int friendX = (int) halfDeviceWidth + (int) (Math.cos(finalAngle) * radius);
            int friendY = (int) halfDeviceHeight + (int) (Math.sin(finalAngle) * radius);
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            params.circleConstraint = R.id.CompassLayout;
            params.circleRadius = 0;
            params.circleAngle = 0;
            params.endToEnd = R.id.MainLayout;
            params.startToStart = R.id.MainLayout;
            params.topToTop = R.id.MainLayout;
            params.bottomToBottom = R.id.MainLayout;
            tv.setLayoutParams(params);
            tv.setHeight(height);
            tv.setWidth(width);

            tv.setX(friendX);
            tv.setY(friendY);
            Context context = tv.getContext();
            Drawable top = context.getResources().getDrawable(R.drawable.friend_triangle);
            tv.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
            tv.setVisibility(View.VISIBLE);
            parent.addView(tv);
            tv.invalidate();
        });
    }

    public void displayFriendLabels(){
        ConstraintLayout parentLayout = activity.findViewById(R.id.MainLayout);
            Log.d("UIManager", Boolean.toString(this.friendDistances.getValue() != null));
            Log.d("FriendSize", Integer.toString(this.friends.size()));

            for (int i = 0; i < this.friends.size(); i++) {
                TextView friend = this.friends.get(i);

                    Double dist = 200.0; //possible null here

                    Degrees angle = new Degrees(i*30);
                    displayFriendLabel(dist.intValue(), (float) angle.getDegrees(), 200, 250, friend, parentLayout);
            }


//        for(TextView friend: friends){
//            float angleDegree = 225;
//            float textAngle = (float) Math.PI / 180 * angleDegree;
//            int textRadius = 300;
//
//            displayFriendLabel(textRadius, textAngle, 100, 250, friend, parentLayout);
//        }
    }
    public void updateUI(Degrees userDirection, List<Degrees> friendOrientation, List<Double> friendDistances){
        if(friendOrientation.size() == 0){
            return;
        }
        /*
        View center = activity.findViewById(R.id.userPosition);
        float centerX = center.getX();
        float centerY = center.getY();
        */
        for(int i = 0 ; i < friends.size() ; i++){
            /*
            Degrees relativeDirection = Degrees.addDegrees(friendOrientation.get(i),userDirection);
            int offsetX = (int) (Math.cos(relativeDirection.getDegrees()-90) * friendDistances.get(i));
            int offsetY = (int) (Math.sin(relativeDirection.getDegrees()-90) * friendDistances.get(i));

            int friendX = (int) centerX + offsetX;
            int friendY = (int) centerY + offsetY;
            friends.get(i).setX(friendX);
            friends.get(i).setY(friendY);
        */
            View curView = friends.get(i);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) curView.getLayoutParams();
            params.circleRadius = friendDistances.get(i).intValue();
            params.circleAngle = (float) Degrees.addDegrees(friendOrientation.get(i),userDirection).getDegrees();

            activity.runOnUiThread(() -> curView.setLayoutParams(params));

        }
    }

    public void destroyTextViews(){
        for(TextView tv: friends){
            ViewGroup parent = ((ViewGroup)tv.getParent());
            if(parent == null) return;
            parent.removeView(tv);
        }
    }


}
