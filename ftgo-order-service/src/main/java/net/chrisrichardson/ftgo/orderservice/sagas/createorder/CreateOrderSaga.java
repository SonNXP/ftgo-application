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
public class CreateOrderSaga implements SimpleSaga<CreateOrderSagaState> {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private SagaDefinition<CreateOrderSagaState> sagaDefinition;

  // The CreateOrderSaga class implements the state machine shown earlier in
  // figure 4.7. It uses the DSL (domain-specific language) provided by the
  // Eventuate Tram Saga framework to define the steps of the Create Order Saga.
  public CreateOrderSaga(OrderServiceProxy orderService, ConsumerServiceProxy consumerService,
      KitchenServiceProxy kitchenService, AccountingServiceProxy accountingService) {
    this.sagaDefinition = step().withCompensation(orderService.reject, CreateOrderSagaState::makeRejectOrderCommand)
        .step()
        .invokeParticipant(consumerService.validateOrder, CreateOrderSagaState::makeValidateOrderByConsumerCommand)
        .step().invokeParticipant(kitchenService.create, CreateOrderSagaState::makeCreateTicketCommand)
        .onReply(CreateTicketReply.class, CreateOrderSagaState::handleCreateTicketReply)
        .withCompensation(kitchenService.cancel, CreateOrderSagaState::makeCancelCreateTicketCommand).step()
        .invokeParticipant(accountingService.authorize, CreateOrderSagaState::makeAuthorizeCommand).step()
        .invokeParticipant(kitchenService.confirmCreate, CreateOrderSagaState::makeConfirmCreateTicketCommand).step()
        .invokeParticipant(orderService.approve, CreateOrderSagaState::makeApproveOrderCommand).build();

  }

  @Override
  public SagaDefinition<CreateOrderSagaState> getSagaDefinition() {
    return sagaDefinition;
  }

}
