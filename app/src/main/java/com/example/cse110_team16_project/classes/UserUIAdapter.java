package com.example.cse110_team16_project.classes;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.example.cse110_team16_project.R;
import com.example.cse110_team16_project.Units.Degrees;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/** Currently unused class previously meant for managing the Location icons on compass.
 * Recommend using ViewGroup instead.
 */
public class UserUIAdapter{




    Activity activity;
    private List<String> friendLabels;
    private LiveData<List<Double>> friendDistances;
    private LiveData<List<Degrees>> friendOrientation;
    private List<TextView> friends;
    public UserUIAdapter(Activity activity, @NonNull LiveData<List<Double>> friendDistances,
                         @NonNull LiveData<List<Degrees>> friendOrientation, List<String> friendLabels,
                         DistanceUpdater distanceUpdater, AbsoluteDirectionUpdater absoluteDirectionUpdater){

        this.activity = activity;
        this.friendDistances = friendDistances;
        this.friendOrientation = friendOrientation;
        this.friendLabels = friendLabels;


    }


    public void populateFriendLabels(List<String> friendLabels, List<TextView> friends) {

        if(friendLabels.size() == 0){
            return;
        }
        for(int i = 0 ; i < friendLabels.size() ; i++) {
            TextView addFriend = new TextView(activity);
            addFriend.setText(friendLabels.get(i));
            friends.add(addFriend);
        }
    }

    public void updateUI(){

    }

    //update the position of the view representing a entity on the compass to the correct direction
    //params View to update, relative direction the entity is from user in degrees
    public void updateIconDirection(TextView tv, Degrees entityDirection) {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) tv.getLayoutParams();
        layoutParams.circleAngle = (float) entityDirection.getDegrees();
        activity.runOnUiThread(() -> tv.setLayoutParams(layoutParams));
    }

    public void updateIconDistance(){

    }


}
