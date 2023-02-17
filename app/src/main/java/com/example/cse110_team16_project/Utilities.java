package com.example.cse110_team16_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.DisplayMetrics;

import java.util.Optional;

public class Utilities {
    public static int getDPScreenWidth(Activity activity){
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        return (int) (displayMetrics.widthPixels / displayMetrics.density);
    }

    public static void showAlert(Activity activity, String message) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);

        alertBuilder
                .setTitle("Alert!")
                .setMessage(message)
                .setPositiveButton("Ok", (dialog, id) -> dialog.cancel())
                .setCancelable(true);

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    public static void showError(Activity activity, String message) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);

        alertBuilder
                .setTitle("Error!")
                .setMessage(message)
                .setPositiveButton("Ok", (dialog, id) -> dialog.cancel())
                .setCancelable(true);

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    public static Optional<Integer> parseCount(String str) {
        try {
            int maxCount = Integer.parseInt(str);
            return Optional.of(maxCount);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
