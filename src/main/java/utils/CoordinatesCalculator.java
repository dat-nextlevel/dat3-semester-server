package utils;

import entities.User;

public class CoordinatesCalculator {

    public static long calcDistance(User u1, User u2) {
        double theta = Double.parseDouble(u1.getY()) - Double.parseDouble(u2.getY());
        double dist = Math.sin(deg2rad(Double.parseDouble(u1.getX()))) * Math.sin(deg2rad(Double.parseDouble(u2.getX()))) +
                Math.cos(deg2rad(Double.parseDouble(u1.getX()))) * Math.cos(deg2rad(Double.parseDouble(u2.getX()))) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1.609344;

        return (Math.round(dist));
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts decimal degrees to radians             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts radians to decimal degrees             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static boolean calcDistanceWithRadius(User u1, User u2, double radius){
        double distance = calcDistance(u1, u2);
        return radius >= distance;
    }

}
