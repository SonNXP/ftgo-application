package net.chrisrichardson.ftgo.orderservice.sagas.createorder;

import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.*;
import net.chrisrichardson.ftgo.kitchenservice.api.CreateTicketReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The saga is orchestrated by the CreateOrderSaga class, which invokes the saga
 * participants using asynchronous request/response. This class keeps track of
 * the process and sends command messages to saga participants, such as Kitchen
 * Service and Consumer Service. The CreateOrderSaga class reads reply messages
 * from its reply channel and then determines the next step, if any, in the
 * saga. Page-122 pdf book
 */
// Order business logic is defined here CreateOrderSaga
public class CreateOrderSaga implements SimpleSaga<CreateOrderSagaState> {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private SagaDefinition<CreateOrderSagaState> sagaDefinition;

  // The CreateOrderSaga class implements the state machine shown earlier in
  // figure 4.7. It uses the DSL (domain-specific language) provided by the
  // Eventuate Tram Saga framework to define the steps of the Create Order Saga.
  // Listing 4.3 The definition of the third step of the saga
  public CreateOrderSaga(OrderServiceProxy orderService, ConsumerServiceProxy consumerService,
      KitchenServiceProxy kitchenService, AccountingServiceProxy accountingService) {
    this.sagaDefinition = step() // step 1: include create order
    .withCompensation(orderService.reject, CreateOrderSagaState::makeRejectOrderCommand)
        .step() // step 2
        // Define the forward transaction.
        .invokeParticipant(consumerService.validateOrder, CreateOrderSagaState::makeValidateOrderByConsumerCommand)
        // 3 third step
        .step() // step 3
        // Define the forward transaction. It creates the CreateTicket command message
        // by calling CreateOrderSagaState.makeCreateTicketCommand() and sends it to
        // the channel specified by kitchenService.create.
        /**
         * public final CommandEndpoint<CreateTicket> create = CommandEndpointBuilder
         * .forCommand(CreateTicket.class)
         * .withChannel(KitchenServiceChannels.COMMAND_CHANNEL)
         * .withReply(CreateTicketReply.class) .build();
         */
        /**
         * CreateOrderSagaState::makeCreateTicketCommand = return new
         * CreateTicket(getOrderDetails().getRestaurantId(), getOrderId(),
         * makeTicketDetails(getOrderDetails()));
         */
        .invokeParticipant(kitchenService.create, CreateOrderSagaState::makeCreateTicketCommand)
        // Call handleCreateTicketReply() when a successful reply is received.
        .onReply(CreateTicketReply.class, CreateOrderSagaState::handleCreateTicketReply)
        // Define the compensating transaction (Rollback when reply is error). The saga
        // executes the compensation transactions in reverse order of the forward
        // transactions - Page 116 pdf book
        // It creates a RejectTicket- Command command message by calling
        // CreateOrderSagaState.makeCancelCreateTicket() and sends it to the channel
        // specified by kitchenService.cancel
        .withCompensation(kitchenService.cancel, CreateOrderSagaState::makeCancelCreateTicketCommand)
        .step() // step 4
        .invokeParticipant(accountingService.authorize, CreateOrderSagaState::makeAuthorizeCommand)
        .step() // step 5
        .invokeParticipant(kitchenService.confirmCreate, CreateOrderSagaState::makeConfirmCreateTicketCommand)
        .step() // step 6
        .invokeParticipant(orderService.approve, CreateOrderSagaState::makeApproveOrderCommand).build();
        // Detail in Table 4.1 The compensating transactions for the Create Order Saga
  }

  @Override
  public SagaDefinition<CreateOrderSagaState> getSagaDefinition() {
    return sagaDefinition;
  }

}
