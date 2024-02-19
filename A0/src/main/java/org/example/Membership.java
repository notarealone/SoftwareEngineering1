package org.example;

public class Membership {
    private final String teamName;
    private final Date beginning;
    private final Date expiration;

    public Membership(String name, Date start, Date end){
        if(name.isEmpty() || start.compareTo(end) >= 0){
            throw new IllegalArgumentException("Invalid value for beginning/expiration date");
        }

        this.teamName = name;
        this.beginning = start;
        this.expiration = end;
    }

    public boolean isOverlap(Date start, Date end){
        return start.compareTo(this.expiration) <= 0 && end.compareTo(this.beginning) >= 0;
    }

    public String getTeamName(){ return this.teamName; }
    public Date getBeginning(){ return this.beginning; }

    public Date getExpiration(){ return this.expiration; }

    public int countDays(){ //TODO : find a more optimized solution!
        int counter = 0;
        Date temp = this.beginning;
        while(temp.compareTo(this.expiration) != 0){
            temp = temp.nextDay();
            counter++;
        }
        return (counter != 0) ? counter+1 : 0;
    }
}
