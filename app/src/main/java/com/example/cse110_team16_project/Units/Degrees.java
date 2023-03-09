package com.example.cse110_team16_project.Units;

/* Class to reduce confusion about units used when talking about direction,
so that we know we are always using degrees instead of radians
*/
public class Degrees {
    private double degrees;
    public Degrees(double degrees) {setDegrees(degrees);}

    public double getDegrees() {return this.degrees;}

    public void setDegrees(double degrees) { this.degrees = degrees; }

    public static Degrees addDegrees(Degrees degrees1, Degrees degrees2){
        return new Degrees(degrees1.getDegrees() + degrees2.getDegrees());
    }
    public static Degrees subtractDegrees(Degrees degrees1, Degrees degrees2){
        return new Degrees(degrees1.getDegrees() - degrees2.getDegrees());
    }
}
