package ir.softwareEng.A1;

public class Deposit {
    private final String accountNo;
    private final int amount;
    public Deposit(String accountNo, int amount){
        this. accountNo = accountNo;
        this.amount = amount;
    }
    public String getAccountNo(){ return this.accountNo; }
    public int getAmount(){ return this.amount; }

    @Override
    public String toString(){
        return "DEPOSIT " + this.accountNo + " " + this.amount;
    }
}
