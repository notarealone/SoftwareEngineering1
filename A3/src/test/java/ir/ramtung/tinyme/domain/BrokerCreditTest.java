package ir.ramtung.tinyme.domain;

import ir.ramtung.tinyme.config.MockedJMSTestConfig;
import ir.ramtung.tinyme.domain.entity.*;
import ir.ramtung.tinyme.domain.service.Matcher;
import ir.ramtung.tinyme.messaging.request.DeleteOrderRq;
import ir.ramtung.tinyme.messaging.request.EnterOrderRq;
import ir.ramtung.tinyme.messaging.request.OrderEntryType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
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
        sellerBroker = Broker.builder().credit(100_000_000L).brokerId(0).build();
        buyerBroker = Broker.builder().credit(100_000_000L).brokerId(1).build();
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
        MatchResult matchResult = matcher.execute(order);
        assertThat(buyerBroker.getCredit()).isEqualTo(100_000_000L - (350 * 15800));
        assertThat(sellerBroker.getCredit()).isEqualTo(100_000_000L + (350 * 15800));
    }
    @Test
    void new_buy_order_matches_partially_with_first_sell_order(){
        Order order = new Order(11, security, Side.BUY, 250, 15800, buyerBroker, shareholder);
        MatchResult matchResult = matcher.execute(order);
        assertThat(buyerBroker.getCredit()).isEqualTo(100_000_000L - (250 * 15800));
        assertThat(sellerBroker.getCredit()).isEqualTo(100_000_000L + (250 * 15800));
    }
    @Test
    void new_buy_order_matches_completely_with_two_top_sell_orders(){
        Order order = new Order(11, security, Side.BUY, 350 + 285, 15810, buyerBroker, shareholder);
        MatchResult matchResult = matcher.execute(order);
        assertThat(buyerBroker.getCredit()).isEqualTo(100_000_000L - ((350 * 15800) + (285 * 15810)));
        assertThat(sellerBroker.getCredit()).isEqualTo(100_000_000L + ((350 * 15800) + (285 * 15810)));
    }
    @Test
    void new_buy_order_matches_partially_with_two_top_sell_orders(){
        Order order = new Order(11, security, Side.BUY, 350 + 250, 15810, buyerBroker, shareholder);
        MatchResult matchResult = matcher.execute(order);
        assertThat(buyerBroker.getCredit()).isEqualTo(100_000_000L - ((350 * 15800) + (250 * 15810)));
        assertThat(sellerBroker.getCredit()).isEqualTo(100_000_000L + ((350 * 15800) + (250 * 15810)));
    }
    @Test
    void new_buy_order_matches_partially_with_first_sell_order_with_remainder(){
        Order order = new Order(11, security, Side.BUY, 350 + 100, 15800, buyerBroker, shareholder);
        MatchResult matchResult = matcher.execute(order);
        assertThat(buyerBroker.getCredit()).isEqualTo(100_000_000L - ((350 * 15800) + (100 * 15800)));
        assertThat(sellerBroker.getCredit()).isEqualTo(100_000_000L + (350 * 15800));
    }
    @Test
    void new_buy_order_matches_partially_with_two_top_sell_orders_with_remainder(){
        Order order = new Order(11, security, Side.BUY, 350 + 1085 + 100, 15810, buyerBroker, shareholder);
        MatchResult matchResult = matcher.execute(order);
        assertThat(buyerBroker.getCredit()).isEqualTo(100_000_000L - ((350 * 15800) + (1085 * 15810) + (100 * 15810)));
        assertThat(sellerBroker.getCredit()).isEqualTo(100_000_000L + ((350 * 15800) + (1085 * 15810)));
    }
    @Test
    void new_buy_order_fails_at_initial_matching_and_stays_in_order_book(){
        Order order = new Order(11, security, Side.BUY, 300, 15790, buyerBroker, shareholder);
        MatchResult matchResult = matcher.execute(order);
        assertThat(buyerBroker.getCredit()).isEqualTo(100_000_000L - (300*15790));
        assertThat(sellerBroker.getCredit()).isEqualTo(100_000_000L);
    }
    @Test
    void new_buy_order_fails_after_matching_partially_with_first_sell_order_by_remainder(){
        Order order = new Order(11, security, Side.BUY, 100000, 15800, buyerBroker, shareholder);
        MatchResult matchResult = matcher.execute(order);
        assertThat(matchResult.outcome()).isEqualTo(MatchResult.notEnoughCredit().outcome());
        assertThat(buyerBroker.getCredit()).isEqualTo(100_000_000L);
        assertThat(sellerBroker.getCredit()).isEqualTo(100_000_000L);
    }
    @Test
    void new_sell_order_fails_at_initial_matching(){
        EnterOrderRq newRq = EnterOrderRq.createNewOrderRq(1, security.getIsin(), 11, LocalDateTime.now(), Side.SELL, 101_000, 15800, 0, 0, 0);
        MatchResult matchResult = security.newOrder(newRq, sellerBroker, shareholder, matcher);
        assertThat(matchResult.outcome()).isEqualTo(MatchResult.notEnoughPositions().outcome());
        assertThat(buyerBroker.getCredit()).isEqualTo(100_000_000L);
        assertThat(sellerBroker.getCredit()).isEqualTo(100_000_000L);
    }
    @Test
    void updated_buy_order_matches_with_multiple_sell_orders(){
        try {
            security.updateOrder(EnterOrderRq.createNewOrderRq(1, security.getIsin(), 1, LocalDateTime.now(), Side.BUY, 450, 15810, 1, 0, 0), matcher);
            assertThat(buyerBroker.getCredit()).isEqualTo(100_000_000L + (304 * 15700) - (350*15800 + 100*15810));
            assertThat(sellerBroker.getCredit()).isEqualTo(100_000_000L + ((350*15800) + (100*15810)));
        } catch (Exception ex) {
            //Not possible
        }
    }
    @Test
    void updated_sell_order_fails_and_gets_canceled(){
        try {
            security.updateOrder(EnterOrderRq.createNewOrderRq(1, security.getIsin(), 6, LocalDateTime.now(), Side.SELL, 101_000, 15810, 0, 0, 0), matcher);
            assertThat(buyerBroker.getCredit()).isEqualTo(100_000_000L);
            assertThat(sellerBroker.getCredit()).isEqualTo(100_000_000L);
        } catch (Exception ex) {
            //Not possible
        }
    }
    @Test //TODO : fails!
    void updating_buy_order_fails_after_partially_matching_so_the_original_order_stays(){
        try {
            MatchResult matchResult = security.updateOrder(EnterOrderRq.createNewOrderRq(1, security.getIsin(), 1, LocalDateTime.now(), Side.BUY, 100_000, 15800, 1, 0, 0), matcher);
            assertThat(matchResult.outcome()).isEqualTo(MatchResult.notEnoughCredit().outcome());
            assertThat(buyerBroker.getCredit()).isEqualTo(100_000_000L);
            assertThat(sellerBroker.getCredit()).isEqualTo(100_000_000L);
        } catch (Exception ex) {
            //Not possible
        }
    }
    @Test
    void buy_order_gets_canceled(){
        try {
            security.deleteOrder(new DeleteOrderRq(1, security.getIsin(), Side.BUY, 1));
            assertThat(buyerBroker.getCredit()).isEqualTo(100_000_000L + (304 * 15700));
            assertThat(sellerBroker.getCredit()).isEqualTo(100_000_000L);
        } catch (Exception ex) {
            //Not possible
        }
    }
    @Test
    void sell_order_gets_canceled(){
        try {
            security.deleteOrder(new DeleteOrderRq(1, security.getIsin(), Side.SELL, 7));
            assertThat(buyerBroker.getCredit()).isEqualTo(100_000_000L);
            assertThat(sellerBroker.getCredit()).isEqualTo(100_000_000L);
        } catch (Exception ex) {
            //Not possible
        }
    }
}