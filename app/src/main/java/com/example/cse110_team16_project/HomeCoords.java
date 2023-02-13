package com.example.cse110_team16_project;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class HomeCoords {
    @PrimaryKey
    @NonNull
    String label;

    double xCoord;
    double yCoord;

    public HomeCoords(String label, double xCoord, double yCoord) {
        this.label = label;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getxCoord() {
        return xCoord;
    }

    public void setxCoord(double xCoord) {
        this.xCoord = xCoord;
    }

    public double getyCoord() {
        return yCoord;
    }

    public void setyCoord(double yCoord) {
        this.yCoord = yCoord;
    }
}
