package net.chrisrichardson.ftgo.orderservice.sagaparticipants;

import io.eventuate.tram.commands.common.Success;
import io.eventuate.tram.sagas.simpledsl.CommandEndpoint;
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder;
import net.chrisrichardson.ftgo.orderservice.api.OrderServiceChannels;

/**
 * Saga participant proxy classes, such as OrderServiceProxy — Each proxy class
 * defines a saga participant’s messaging API, which consists of the command
 * channel, the command message types, and the reply types.
 */
// Each service in system should has a proxy (đại diện)
// Each proxy contains endpoints. Page 139 pdf book
// Each CommandEndpoint specifies the command type, the command message’s
// destination channel, and the expected reply types.
public class OrderServiceProxy {

	public final CommandEndpoint<RejectOrderCommand> reject = CommandEndpointBuilder
			.forCommand(RejectOrderCommand.class)
			.withChannel(OrderServiceChannels.COMMAND_CHANNEL)
			.withReply(Success.class).build();

	public final CommandEndpoint<ApproveOrderCommand> approve = CommandEndpointBuilder
			.forCommand(ApproveOrderCommand.class)
			.withChannel(OrderServiceChannels.COMMAND_CHANNEL)
			.withReply(Success.class).build();

}

/** Proxy classes, such as KitchenServiceProxy, aren’t strictly necessary. A saga could simply
send command messages directly to participants. But proxy classes have two important
benefits. First, a proxy class defines static typed endpoints, which reduces the chance
of a saga sending the wrong message to a service. Second, a proxy class is a well-defined
API for invoking a service that makes the code easier to understand and test. For example,
chapter 10 describes how to write tests for KitchenServiceProxy that verify that
Order Service correctly invokes Kitchen Service. Without KitchenServiceProxy, it
would be impossible to write such a narrowly scoped test. */