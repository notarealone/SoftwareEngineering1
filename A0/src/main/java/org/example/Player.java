package org.example;

import java.util.ArrayList;

public class Player {
    private final String name;
    private ArrayList<Membership> membershipHistory;

    public Player(String playerName){
        if(playerName.isEmpty())
            throw new IllegalArgumentException("Name should not be an empty string");

        this.name = playerName;
        this.membershipHistory = new ArrayList<Membership>();
    }

    public void addMembership(Membership membership){
        for(Membership mem : this.membershipHistory) {
            if(membership.isOverlap(mem.getBeginning(), mem.getExpiration())){
                throw new IllegalArgumentException("New Membership has conflict with previous ones");
            }
        }
        this.membershipHistory.add(membership);
    }
}
