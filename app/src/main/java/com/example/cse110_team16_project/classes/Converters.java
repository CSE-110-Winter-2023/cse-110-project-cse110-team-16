package com.example.cse110_team16_project.classes;

import androidx.room.TypeConverter;

public class Converters {
    @TypeConverter
    public static Coordinates StringToCoordinates(String str){
        if (str == null) return null;
        String[] data = str.split(",",2);
        return new Coordinates(Double.valueOf(data[0]), Double.valueOf(data[1]));
    }

    @TypeConverter
    public static String CoordinatesToString(Coordinates c){
        return c == null ? null : c.toString();
    }
}
