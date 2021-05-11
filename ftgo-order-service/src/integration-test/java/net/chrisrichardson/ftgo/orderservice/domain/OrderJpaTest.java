package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.orderservice.OrderDetailsMother;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.support.TransactionTemplate;

import static net.chrisrichardson.ftgo.orderservice.OrderDetailsMother.CONSUMER_ID;
import static net.chrisrichardson.ftgo.orderservice.OrderDetailsMother.chickenVindalooLineItems;
import static net.chrisrichardson.ftgo.orderservice.RestaurantMother.AJANTA_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderJpaTestConfiguration.class)
public class OrderJpaTest {

  @Autowired
  private OrderRepository orderRepository;

  // TransactionTemplate provides a set of callback-based APIs to manage
  // transactions manually.
  @Autowired
  private TransactionTemplate transactionTemplate;

  // The shouldSaveAndLoadOrder() test method executes two transactions. The first
  // saves a newly created Order in the database. The second transaction loads the
  // Order and verifies that its fields are properly initialized.
  @Test
  public void shouldSaveAndLoadOrder() {

    // Execute the action specified by the given callback object within a
    // transaction. Allows for returning a result object created within the
    // transaction, that is, a domain object or a collection of domain objects. A
    // RuntimeException thrown by the callback is treated as a fatal exception that
    // enforces a rollback. Such an exception gets propagated to the caller of the
    // template.
    long orderId = transactionTemplate.execute((ts) -> {
      Order order = new Order(CONSUMER_ID, AJANTA_ID, OrderDetailsMother.DELIVERY_INFORMATION,
          chickenVindalooLineItems());
      // run an instance of the database during testing is to use Docker.
      orderRepository.save(order);
      return order.getId();
    });

    transactionTemplate.execute((ts) -> {
      Order order = orderRepository.findById(orderId).get();

      assertNotNull(order);
      assertEquals(OrderState.APPROVAL_PENDING, order.getState());
      assertEquals(AJANTA_ID, order.getRestaurantId());
      assertEquals(CONSUMER_ID, order.getConsumerId().longValue());
      assertEquals(chickenVindalooLineItems(), order.getLineItems());
      return null;
    });

  }

}
