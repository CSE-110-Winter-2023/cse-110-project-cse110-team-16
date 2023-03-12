package com.example.cse110_team16_project.classes.CoordinateClasses;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/*
SCLocation class is responsible for storing information about the SCLocation's location and name.
It is NOT responsible for updating the SCLocation position, this is currently handled in DeviceTracker.

 */

@Entity(tableName = "location")
public class SCLocation extends CoordinateEntity {

    @SerializedName("label")
    @NonNull
    private String label;

    @PrimaryKey
    @SerializedName("public_code")
    @NonNull
    public final String public_code;

@Ignore
    public SCLocation(Coordinates coordinates, @NonNull String label, @NonNull String public_code){
        super(coordinates);
        this.label = label;
        this.public_code = public_code;
    }

    public SCLocation(double latitude, double longitude, @NonNull String label, @NonNull String public_code){
        super(latitude, longitude);
        this.label = label;
        this.public_code = public_code;
    }

    //most useful for storing user inputs
    @Ignore
    public SCLocation(@NonNull String label, @NonNull String public_code){
        super(0,0);
        this.label = label;
        this.public_code = public_code;
    }

    @NonNull
    public String getLabel() { return this.label;}
    public void setLabel(@NonNull String label) { this.label = label;}

    @NonNull
    public String getPublicCode() {
        return public_code;
    }

    public static SCLocation fromJSON(String json) {
        if(!json.contains("public_code")) return null;
        return new Gson().fromJson(json,SCLocation.class);
    }

    public String toJSON() {
        return new Gson().toJson(this);
    }

}
