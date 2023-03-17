package com.example.cse110_team16_project.classes.UI;

import static com.example.cse110_team16_project.classes.Updaters.ScreenDistanceUpdater.LARGEST_RADIUS;

import android.app.Activity;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.example.cse110_team16_project.R;
import com.example.cse110_team16_project.classes.Misc.Converters;
import com.example.cse110_team16_project.classes.Units.Degrees;
import com.example.cse110_team16_project.classes.Units.Radians;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



/** Currently unused class previously meant for managing the Location icons on compass.
 * Recommend using ViewGroup instead.
 */
public class UserIconManager {
    private final ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    Activity activity;
    private List<String> friendLabels;
    private List<TextView> friends;



    public UserIconManager(Activity activity, @NonNull LiveData<List<Double>> friendDistances,
                           @NonNull LiveData<List<Degrees>> friendOrientation
                         , LiveData<Radians> userOrientation){
        this.friends = new ArrayList<>();
        this.activity = activity;

        friendDistances.observe((LifecycleOwner) activity,
                    distances -> backgroundThreadExecutor.submit(() -> {
                        updateUI(Converters.RadiansToDegrees(userOrientation.getValue()),friendOrientation.getValue(),distances);
                        return null;
                    })
            );

        userOrientation.observe((LifecycleOwner) activity,
                orientation -> backgroundThreadExecutor.submit(() -> {
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
            addFriend.setTextColor(Color.BLUE);
            addFriend.setMaxLines(1);
            addFriend.setEllipsize(TextUtils.TruncateAt.END);
            addFriend.setId(View.generateViewId());
            friends.add(addFriend);
        }
        displayFriendLabels();
    }


    public void displayFriendLabel(TextView tv){
        ConstraintLayout parentLayout = activity.findViewById(R.id.MainLayout);
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, 50);
            params.circleConstraint = R.id.CompassLayout;
            params.circleRadius = 999;
            params.circleAngle = 90;
            params.startToStart = R.id.MainLayout;
            params.topToTop = R.id.MainLayout;
            tv.setLayoutParams(params);
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

        if(friends.size() != friendOrientation.size() || friends.size() != friendDistances.size()) return;
        IconStacker stacker = new IconStacker(userDirection, friendOrientation, friendDistances);
        stacker.adjustIcons();
        List<Degrees> adjustAngles = stacker.getAdjustedAngles();
        List<Double> adjustedRadius = stacker.getAdjustedRadius();
        //List<Degrees> adjustAngles = stacker.getRegularAngles();
        //List<Double> adjustedRadius = stacker.getRegularRadius();
        IconTruncater truncater = new IconTruncater(adjustAngles, adjustedRadius, friends);
        truncater.truncateIcons();
        List<Integer> finalWidth = truncater.getFinalWidth();
        adjustAngles = truncater.getAdjustedAngles();
        adjustedRadius = truncater.getAdjustedRadius();
        for(int i = 0 ; i < friends.size() ; i++){
            TextView curView = friends.get(i);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) curView.getLayoutParams();
            params.circleRadius = adjustedRadius.get(i).intValue();
            params.circleAngle = (float) adjustAngles.get(i).getDegrees();
            if(params.circleRadius == LARGEST_RADIUS) {
                params.circleRadius += 13;
                activity.runOnUiThread(() -> {
                    curView.setText("⬤");
                    curView.setWidth(30);
                    curView.setTextColor(Color.RED);
                });
            }
            else {
                String reAddLabel = friendLabels.get(i);
                int newWidth = finalWidth.get(i);
                activity.runOnUiThread(() -> {
                    curView.setText(reAddLabel);
                    curView.setWidth(newWidth);
                    curView.setTextColor(Color.BLUE);
                });
            }
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

    public List<TextView> getTextViews(){
        return this.friends;
    }

}
