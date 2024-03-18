package ir.ramtung.tinyme.domain;

import ir.ramtung.tinyme.config.MockedJMSTestConfig;
import ir.ramtung.tinyme.domain.entity.*;
import ir.ramtung.tinyme.domain.service.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(MockedJMSTestConfig.class)
@DirtiesContext
public class BrokerCreditTest {
    private Security security;
    private Broker sellerBroker;
    private Broker buyerBroker;
    private Shareholder shareholder;
    private OrderBook orderBook;
    private List<Order> orders;
    @Autowired
    private Matcher matcher;

    @BeforeEach
    void setupOrderBook() {
        security = Security.builder().build();
        sellerBroker = Broker.builder().credit(100_000_000L).build();
        buyerBroker = Broker.builder().credit(100_000_000L).build();
        shareholder = Shareholder.builder().build();
        shareholder.incPosition(security, 100_000);
        orderBook = security.getOrderBook();
        orders = Arrays.asList(
                new Order(1, security, Side.BUY, 304, 15700, buyerBroker, shareholder),
                new Order(2, security, Side.BUY, 43, 15500, buyerBroker, shareholder),
                new Order(3, security, Side.BUY, 445, 15450, buyerBroker, shareholder),
                new Order(4, security, Side.BUY, 526, 15450, buyerBroker, shareholder),
                new Order(5, security, Side.BUY, 1000, 15400, buyerBroker, shareholder),

                new Order(6, security, Side.SELL, 350, 15800, sellerBroker, shareholder),
                new Order(7, security, Side.SELL, 285, 15810, sellerBroker, shareholder),
                new Order(8, security, Side.SELL, 800, 15810, sellerBroker, shareholder),
                new Order(9, security, Side.SELL, 340, 15820, sellerBroker, shareholder),
                new Order(10, security, Side.SELL, 65, 15820, sellerBroker, shareholder)
        );
        orders.forEach(order -> orderBook.enqueue(order));
    }

}