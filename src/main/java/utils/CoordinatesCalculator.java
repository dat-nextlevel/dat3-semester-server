package utils;

import entities.User;

public class CoordinatesCalculator {

    public double calcDistance(User user1, User user2){
        return Math.sqrt(Math.pow((Double.parseDouble(user2.getX())- Double.parseDouble(user1.getX())), 2)
                + (Math.pow((Double.parseDouble(user2.getY())-Double.parseDouble(user1.getY())), 2)));
    }

}
