package ir.softwareEng.A1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

@Component
public class Controller {
    public static Accounting manager;
    private final JmsTemplate jmsTemplate;
    @Autowired
    public Controller(JmsTemplate jmsTemplate){
        this.jmsTemplate = jmsTemplate;
    }

    @JmsListener(destination = "INQ")
    public void getMsg(String content){
        String[] inputMsg = content.split("\\s+");
        respond(inputMsg);
    }

    public void respond(String[] inputMsg){
        switch (inputMsg[0]){
            case "DEPOSIT" :
                manager.deposit(inputMsg[1], Integer.parseInt(inputMsg[2]));
                jmsTemplate.convertAndSend("OUTQ", "0 Deposit successful");
                break;
            case "WITHDRAW" :
                switch(manager.withdraw(inputMsg[1], Integer.parseInt(inputMsg[2]))){
                    case 0 :
                        jmsTemplate.convertAndSend("OUTQ", "0 Withdraw successful");
                        break;
                    case 1 :
                        jmsTemplate.convertAndSend("OUTQ", "1 Insufficient funds");
                        break;
                    case 2 :
                        jmsTemplate.convertAndSend("OUTQ", "2 Unknown account number");
                        break;
                }
                break;
            case "BALANCE" :
                int accountBalance = manager.getBalance(inputMsg[1]);
                if(accountBalance < 0) {
                    jmsTemplate.convertAndSend("OUTQ", "2 Unknown account number");
                }
                else {
                    jmsTemplate.convertAndSend("OUTQ", "0 Balance: " + accountBalance);
                }
                break;
        }
    }
    
}
