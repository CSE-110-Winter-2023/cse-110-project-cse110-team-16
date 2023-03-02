package com.example.cse110_team16_project.classes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.annotations.SerializedName;

public abstract class CoordinateEntity {

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    public CoordinateEntity(Coordinates coordinates) {
        setCoordinates(coordinates);
    }

    public void setCoordinates(Coordinates coordinates) {
        if(coordinates != null) {
            this.latitude = coordinates.getLatitude();
            this.longitude = coordinates.getLongitude();
        }
    }

    public Coordinates getCoordinates() {
        return new Coordinates(latitude, longitude);
    }

}
