package org.example;

import java.util.ArrayList;

public class Player {
    private final String name;
    private ArrayList<Membership> membershipHistory = new ArrayList<Membership>();

    public Player(String playerName){
        if(playerName.isEmpty())
            throw new IllegalArgumentException("Name should not be empty");

        this.name = playerName;
    }

    public void addMembership(Membership membership){
        //TODO : Handle membership overlaps (probably by adding a compareTo method for Membership)
    }
}
