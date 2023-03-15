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
    private List<TextView> friends;


    public UserUIAdapter(Activity activity, @NonNull LiveData<List<Double>> friendDistances,
                         @NonNull LiveData<List<Degrees>> friendOrientation
                         , LiveData<Radians> userOrientation){
        this.friends = new ArrayList<>();
        this.activity = activity;
        this.friendDistances = friendDistances;

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
            friends.add(addFriend);
        }
        displayFriendLabels();
    }


    public void displayFriendLabel(TextView tv){
        ConstraintLayout parentLayout = activity.findViewById(R.id.MainLayout);
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            params.circleConstraint = R.id.CompassLayout;
            params.circleRadius = 999;
            params.circleAngle = 90;
            params.startToStart = R.id.MainLayout;
            params.topToTop = R.id.MainLayout;
            tv.setLayoutParams(params);
            Context context = activity;
            Drawable top = context.getResources().getDrawable(R.drawable.friend_triangle);
            tv.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
            parentLayout.addView(tv);
    }

    public void displayFriendLabels(){
         for (int i = 0; i < this.friends.size(); i++) {
                    displayFriendLabel(this.friends.get(i));
            }
    }
    public void updateUI(Degrees userDirection, List<Degrees> friendOrientation, List<Double> friendDistances){
        if(friendOrientation.size() == 0){
            return;
        }

        for(int i = 0 ; i < friends.size() ; i++){
            View curView = friends.get(i);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) curView.getLayoutParams();
            params.circleRadius = friendDistances.get(i).intValue();
            params.circleAngle = (float) Degrees.subtractDegrees(friendOrientation.get(i),userDirection).getDegrees();

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
