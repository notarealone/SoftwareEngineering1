package org.example;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.util.ArrayList;

public class CSVManager {
    private final FileReader reader;
    private final CSVReader csvReader;
    public ArrayList<Player> playersData;

    public CSVManager(String path) throws Exception {
        this.reader = new FileReader(path);
        this.csvReader = new CSVReader(this.reader);
        playersData = new ArrayList<Player>();
    }

    public void readData(){
        String[] nextRecord ;
        try {
            while ((nextRecord = csvReader.readNext()) != null) {
                Player newRecord = new Player(nextRecord[0]);
                Date membershipStart = new Date(Integer.parseInt(nextRecord[2]), Integer.parseInt(nextRecord[3]), Integer.parseInt(nextRecord[4]));
                Date membershipEnd = new Date(Integer.parseInt(nextRecord[5]), Integer.parseInt(nextRecord[6]), Integer.parseInt(nextRecord[7]));
                Membership newMembership = new Membership(nextRecord[1], membershipStart, membershipEnd);
                addPlayerData(newRecord, newMembership);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addPlayerData(Player newRecord, Membership newSub){
        for(Player player : this.playersData){
            if(newRecord.getName().equals(player.getName())){
                try {
                    player.addMembership(newSub);
                }
                catch (IllegalArgumentException e) {
                    //Unreachable ->
                }
                return;
            }
        }

        newRecord.addMembership(newSub);
        this.playersData.add(newRecord);
    }

    public int countMembershipDays(String playerName, String teamName){
        for(Player player : this.playersData){
            if(player.getName().equals(playerName)){
                return player.countMembershipDays.apply(teamName);
            }
        }
        return -1;
    }

}
