package org.example;

public class Main {
    public static void main(String[] args) {
        Date step_3 = new Date(18, 11, 1402);
        System.out.println(step_3.nextDay().toString());

        try {
            CSVManager myManager = new CSVManager("src/main/data/input.csv");
            myManager.readData();
            System.out.println(myManager.countMembershipDays("Gholam", "Golgohar"));
        }
        catch (Exception e){
            //Unreachable
        }
    }
}