package org.example;

import java.util.ArrayList;
import java.util.function.Function;

public class Player {
    private final String name;
    private ArrayList<Membership> membershipHistory;

    public Player(String playerName){
        if(playerName.isEmpty())
            throw new IllegalArgumentException("Name should not be an empty string");

        this.name = playerName;
        this.membershipHistory = new ArrayList<Membership>();
    }

    public String getName(){ return this.name; }

    public void addMembership(Membership membership){
        for(Membership mem : this.membershipHistory) {
            if(membership.isOverlap(mem.getBeginning(), mem.getExpiration())){
                throw new IllegalArgumentException("New Membership has conflict with previous ones");
            }
        }
        this.membershipHistory.add(membership);
    }

    public Function<String, Integer> countMembershipDays = (teamName) -> this.membershipHistory.stream() .filter(membership -> membership.getTeamName().equals(teamName)) .mapToInt(Membership::countDays) .sum();
}
