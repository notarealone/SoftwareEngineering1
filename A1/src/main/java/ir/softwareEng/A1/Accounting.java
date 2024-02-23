package ir.softwareEng.A1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service("manager")
public class Accounting {
    private static ArrayList<Account> accounts;
    @Autowired
    public Accounting(){
        accounts = new ArrayList<Account>();
    }
    public void deposit(String accountNo, int amount){
        for(Account acnt : accounts){
            if(acnt.getAccountNo().equals(accountNo)){
                acnt.deposit(amount);
                return;
            }
        }
        accounts.add(new Account(accountNo, amount));
    }

    public int withdraw(String accountNo, int amount){
        for(Account acnt : accounts){
            if(acnt.getAccountNo().equals(accountNo)){
                if(acnt.getBalance() >= amount){
                    acnt.withdraw(amount);
                    return 0;
                }
                else {
                    return 1;
                }
            }
        }
        return 2;
    }

    public int getBalance(String accountNo){
        for(Account acnt : accounts){
            if(acnt.getAccountNo().equals(accountNo)){
                return acnt.getBalance();
            }
        }
        return -1;
    }
}
