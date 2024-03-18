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
    @Test
    void new_buy_order_matches_completely_with_first_sell_order(){
        Order order = new Order(11, security, Side.BUY, 350, 15800, buyerBroker, shareholder);
        MatchResult matchResult = matcher.match(order);
        assertThat(buyerBroker.getCredit()).isEqualTo(100_000_000L - (350 * 15800));
        assertThat(sellerBroker.getCredit()).isEqualTo(100_000_000L + (350 * 15800));
    }
    @Test
    void new_buy_order_matches_partially_with_first_sell_order(){
        Order order = new Order(11, security, Side.BUY, 250, 15800, buyerBroker, shareholder);
        MatchResult matchResult = matcher.match(order);
        assertThat(buyerBroker.getCredit()).isEqualTo(100_000_000L - (250 * 15800));
        assertThat(sellerBroker.getCredit()).isEqualTo(100_000_000L + (250 * 15800));
    }
    @Test
    void new_buy_order_matches_completely_with_two_top_sell_orders(){
        Order order = new Order(11, security, Side.BUY, 350 + 285, 15810, buyerBroker, shareholder);
        MatchResult matchResult = matcher.match(order);
        assertThat(buyerBroker.getCredit()).isEqualTo(100_000_000L - ((350 * 15800) + (285 * 15810)));
        assertThat(sellerBroker.getCredit()).isEqualTo(100_000_000L + ((350 * 15800) + (285 * 15810)));
    }
    @Test
    void new_buy_order_matches_partially_with_two_top_sell_orders(){
        Order order = new Order(11, security, Side.BUY, 350 + 250, 15810, buyerBroker, shareholder);
        MatchResult matchResult = matcher.match(order);
        assertThat(buyerBroker.getCredit()).isEqualTo(100_000_000L - ((350 * 15800) + (250 * 15810)));
        assertThat(sellerBroker.getCredit()).isEqualTo(100_000_000L + ((350 * 15800) + (250 * 15810)));
    }
    @Test //TODO : fail because of remainder?
    void new_buy_order_matches_partially_with_first_sell_order_with_remainder(){
        Order order = new Order(11, security, Side.BUY, 350 + 100, 15800, buyerBroker, shareholder);
        MatchResult matchResult = matcher.match(order);
        assertThat(buyerBroker.getCredit()).isEqualTo(100_000_000L - ((350 * 15800) + (100 * 15800)));
        assertThat(sellerBroker.getCredit()).isEqualTo(100_000_000L + (350 * 15800));
    }
    @Test //TODO : fail because of remainder?
    void new_buy_order_matches_partially_with_two_top_sell_orders_with_remainder(){
        Order order = new Order(11, security, Side.BUY, 350 + 285 + 100, 15810, buyerBroker, shareholder);
        MatchResult matchResult = matcher.match(order);
        assertThat(buyerBroker.getCredit()).isEqualTo(100_000_000L - ((350 * 15800) + (285 * 15810) + (100 * 15810)));
        assertThat(sellerBroker.getCredit()).isEqualTo(100_000_000L + ((350 * 15800) + (285 * 15810)));
    }
}