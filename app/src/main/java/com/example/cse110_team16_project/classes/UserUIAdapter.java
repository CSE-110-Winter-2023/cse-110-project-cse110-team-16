package com.example.cse110_team16_project.classes;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cse110_team16_project.R;
import com.example.cse110_team16_project.Units.Degrees;
import com.example.cse110_team16_project.Units.Radians;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


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

    private OrientationService orientationService;

    private  Radians userDirection;

    public UserUIAdapter(Activity activity, @NonNull LiveData<List<Double>> friendDistances,
                         @NonNull LiveData<List<Degrees>> friendOrientation, List<String> friendLabels
                         , OrientationService orientationService){

        this.activity = activity;
        this.friendDistances = friendDistances;
        this.friendOrientation = friendOrientation;
        this.friendLabels = friendLabels;
        populateFriendLabels(friendLabels);

        orientationService.getOrientation().observe((LifecycleOwner) activity,
                userOrientation -> this.future = backgroundThreadExecutor.submit(() -> {
                    userDirection = userOrientation;
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
            TextView addFriend = new TextView(activity);
            addFriend.setText(friendLabels.get(i));
            friends.add(addFriend);
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


}
