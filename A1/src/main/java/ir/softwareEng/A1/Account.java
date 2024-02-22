package ir.softwareEng.A1;

import java.util.AbstractCollection;

public class Account {
    private int balance;
    private String accountNo;
    public Account(String accountNo){
        this.accountNo = accountNo;
    }
    public Account(String accountNo, int balance){
        this.accountNo = accountNo;
        this.balance = balance;
    }
    public String getAccountNo(){ return this.accountNo; }
    public int getBalance(){ return this.balance; }
    public void deposit(int amount){
        this.balance = this.balance + amount;
    }
    public void withdraw(int amount){
        this.balance = this.balance - amount;
    }
}
