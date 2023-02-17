package com.example.cse110_team16_project.Room;

import android.location.Location;

import androidx.room.TypeConverter;

import com.example.cse110_team16_project.classes.Coordinates;

public class Converters {
    @TypeConverter
    public static Coordinates StringToCoordinates(String str){
        if (str == null) return null;
        String[] data = str.split(",",2);
        return new Coordinates(Double.parseDouble(data[0]), Double.parseDouble(data[1]));
    }

    @TypeConverter
    public static String CoordinatesToString(Coordinates c){
        return c == null ? null : c.first + "," + c.second;
    }

    public static Coordinates LocationToCoordinates(Location location){
        return new Coordinates(location.getLatitude(),location.getLongitude());
    }
}
