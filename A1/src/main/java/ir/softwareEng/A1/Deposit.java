package ir.softwareEng.A1;

public class Deposit {
    private String accountNo;
    private int amount;
    public Deposit(String accountNo, int amount){
        this. accountNo = accountNo;
        this.amount = amount;
    }
    @Override
    public String toString(){
        return "DEPOSIT " + this.accountNo + " " + this.amount;
    }
}
