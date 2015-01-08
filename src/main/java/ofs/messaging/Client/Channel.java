/**
 * 
 */
package ofs.messaging.Client;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Ramanan Natarajan
 *
 */

public interface Channel {

	public com.rabbitmq.client.Channel createChannel();

	/**
	 * @param exchange
	 * @param type
	 *            Exchange is the name of the exchange and type defines one of topic or queues as
	 *            defined by the enum ExchangeType
	 */

	public void exchangeDeclare(String exchange, ExchangeType type);

	/**
	 * @param exchange
	 * 
	 *            Exchange is the name of the exchange and type defaults to topic
	 */
	// FIXME: to appropriately javadoc this exchange type
	public void exchangeDeclare(String exchange);

	/**
	 * @param exchange
	 * @param type
	 * @param durable
	 * @param autoDelete
	 */
	public void exchangeDeclare(String exchange, ExchangeType type, boolean durable,
			boolean autoDelete);

	/**
	 * @param exchange
	 * @param type
	 * @param durable
	 * @param autoDelete
	 * @param internal
	 * @param arguments
	 */
	public void exchangeDeclare(String exchange, ExchangeType type, boolean durable,
			boolean autoDelete, boolean internal, Map<String, Object> arguments);

	// FIXME: understand what this mandatory flag means?. rabbit mq doesnt support immediate
	// set default to false;
	public void basicPublish(String exchange, String routingKey, boolean mandatory,
			boolean immediate, byte[] body);

	/**
	 * @param exchange
	 * @param routingKey
	 * @param mandatory
	 * @param immediate
	 * @param props
	 * @param body
	 */
	public void basicPublish(String exchange, String routingKey, boolean mandatory,
			boolean immediate, com.rabbitmq.client.AMQP.BasicProperties props, byte[] body);

	/**
	 * @param exchange
	 * @param routingKey
	 * @param body
	 */
	public void basicPublish(String exchange, String routingKey, byte[] body);

	/**
	 * This method closes the Channel
	 */
	public void close();

	String basicConsume(String queue, Consumer callback) throws IOException;

	/**
	 * Start a non-nolocal, non-exclusive consumer, with a server-generated consumerTag.
	 * 
	 * @param queue
	 *            the name of the queue
	 * @param autoAck
	 *            true if the server should consider messages acknowledged once delivered; false if
	 *            the server should expect explicit acknowledgements
	 * @param callback
	 *            an interface to the consumer object
	 * @return the consumerTag generated by the server
	 * @throws java.io.IOException
	 *             if an error is encountered
	 * @see com.rabbitmq.client.AMQP.Basic.Consume
	 * @see com.rabbitmq.client.AMQP.Basic.ConsumeOk
	 * @see #basicConsume(String, boolean, String, boolean, boolean, Map, Consumer)
	 */
	String basicConsume(String queue, boolean autoAck, Consumer callback) throws IOException;

	/**
	 * Start a non-nolocal, non-exclusive consumer, with a server-generated consumerTag and
	 * specified arguments.
	 * 
	 * @param queue
	 *            the name of the queue
	 * @param autoAck
	 *            true if the server should consider messages acknowledged once delivered; false if
	 *            the server should expect explicit acknowledgements
	 * @param arguments
	 *            a set of arguments for the consume
	 * @param callback
	 *            an interface to the consumer object
	 * @return the consumerTag generated by the server
	 * @throws java.io.IOException
	 *             if an error is encountered
	 * @see com.rabbitmq.client.AMQP.Basic.Consume
	 * @see com.rabbitmq.client.AMQP.Basic.ConsumeOk
	 * @see #basicConsume(String, boolean, String, boolean, boolean, Map, Consumer)
	 */
	String basicConsume(String queue, boolean autoAck, Map<String, Object> arguments,
			Consumer callback) throws IOException;

	/**
	 * Start a non-nolocal, non-exclusive consumer.
	 * 
	 * @param queue
	 *            the name of the queue
	 * @param autoAck
	 *            true if the server should consider messages acknowledged once delivered; false if
	 *            the server should expect explicit acknowledgements
	 * @param consumerTag
	 *            a client-generated consumer tag to establish context
	 * @param callback
	 *            an interface to the consumer object
	 * @return the consumerTag associated with the new consumer
	 * @throws java.io.IOException
	 *             if an error is encountered
	 * @see com.rabbitmq.client.AMQP.Basic.Consume
	 * @see com.rabbitmq.client.AMQP.Basic.ConsumeOk
	 * @see #basicConsume(String, boolean, String, boolean, boolean, Map, Consumer)
	 */
	String basicConsume(String queue, boolean autoAck, String consumerTag, Consumer callback)
			throws IOException;

	/**
	 * Start a consumer. Calls the consumer's {@link Consumer#handleConsumeOk} method.
	 * 
	 * @param queue
	 *            the name of the queue
	 * @param autoAck
	 *            true if the server should consider messages acknowledged once delivered; false if
	 *            the server should expect explicit acknowledgements
	 * @param consumerTag
	 *            a client-generated consumer tag to establish context
	 * @param noLocal
	 *            true if the server should not deliver to this consumer messages published on this
	 *            channel's connection
	 * @param exclusive
	 *            true if this is an exclusive consumer
	 * @param callback
	 *            an interface to the consumer object
	 * @param arguments
	 *            a set of arguments for the consume
	 * @return the consumerTag associated with the new consumer
	 * @throws java.io.IOException
	 *             if an error is encountered
	 * @see com.rabbitmq.client.AMQP.Basic.Consume
	 * @see com.rabbitmq.client.AMQP.Basic.ConsumeOk
	 */
	String basicConsume(String queue, boolean autoAck, String consumerTag, boolean noLocal,
			boolean exclusive, Map<String, Object> arguments, Consumer callback) throws IOException;

	/**
	 * Cancel a consumer. Calls the consumer's {@link Consumer#handleCancelOk} method.
	 * 
	 * @param consumerTag
	 *            a client- or server-generated consumer tag to establish context
	 * @throws IOException
	 *             if an error is encountered, or if the consumerTag is unknown
	 * @see com.rabbitmq.client.AMQP.Basic.Cancel
	 * @see com.rabbitmq.client.AMQP.Basic.CancelOk
	 * */
	void basicCancel(String consumerTag) throws IOException;

}
