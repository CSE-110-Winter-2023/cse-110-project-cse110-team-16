package com.example.cse110_team16_project.classes;

import androidx.annotation.NonNull;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.time.Instant;

/*
SCLocation class is responsible for storing information about the SCLocation's location and name.
It is NOT responsible for updating the SCLocation position, this is currently handled in DeviceTracker.

coordinates is not allowed to be null and if a null is passed to setCoordinates, user will not update
 */

@Entity(tableName = "location")
public class SCLocation extends CoordinateEntity {

    @SerializedName("label")
    @NonNull
    private String label;

    @PrimaryKey
    @SerializedName("public_code")
    @NonNull
    private final String public_code;

    public SCLocation(Coordinates coordinates, String label, String public_code){
        super(coordinates);
        this.label = label;
        this.public_code = public_code;
    }


    public String getLabel() { return this.label;}
    public void setLabel(String label) { this.label = label;}

    @NonNull
    public String getPublicCode() {
        return public_code;
    }

    public static SCLocation fromJSON(String json) {
        return new Gson().fromJson(json,SCLocation.class);
    }

    public String toJSON() {
        return new Gson().toJson(this);
    }
}
