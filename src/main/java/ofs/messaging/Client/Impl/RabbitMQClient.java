/**
 * 
 */
package ofs.messaging.Client.Impl;

import ofs.messaging.DataStore;
import ofs.messaging.Util;
import ofs.messaging.Client.MessagingClient;
import ofs.messaging.Client.Exceptions.MessagePublishingFailedException;

import java.nio.channels.InterruptedByTimeoutException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ramanan Natarajan
 *
 */
public class RabbitMQClient implements MessagingClient {

	@SuppressWarnings("unused")
	private String description;
	private String clientName;
	private ExecutorService executorService;

	/*
	 * XXX: Also, when we create a client, we can return the Id, and then on registration take the
	 * clientId and register and return back the exchange to which it has to be published
	 */

	/**
	 * @return the executorService
	 */
	public ExecutorService getExecutorService() {
		return executorService;
	}

	/**
	 * @param executorService
	 *            the executorService to set
	 */
	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	/**
	 * 
	 */
	// FIXME: see what the various arguments for this and add appropriately
	public RabbitMQClient(String clientName, String description) {
		this.clientName = clientName;
		this.description = description;

	}

	public RabbitMQClient() {

	}

	public RabbitMQClient getInstance(String clientName, String description) {

		RabbitMQClient client = new RabbitMQClient(clientName, description);
		if (this.executorService == null) {
			// We want at least 4 threads, even if we only have 2 CPUS.
			int nThreads = Math.max(4, Runtime.getRuntime().availableProcessors() / 2);

			final ThreadFactory clientThreadFactory = new ThreadFactory() {
				private final AtomicInteger threadNumber = new AtomicInteger(1);

				public Thread newThread(Runnable r) {
					final Thread thread = new Thread(r, "client-" + threadNumber.getAndIncrement());
					thread.setDaemon(true);
					return thread;
				}
			};
			this.executorService = Executors.newFixedThreadPool(nThreads, clientThreadFactory);

		}
		return this;

	}

	private UUID clientId;

	/*
	 * public ofs.messaging.Client.Connection Connect() {
	 * 
	 * return null; }
	 */
	public boolean Publish() {

		return false;
	}

	public boolean Consume() {

		return false;
	}

	/**
	 * @return the clientId
	 */
	public UUID getClientId() {
		return clientId;
	}

	/**
	 * @param eventId
	 *            this takes in an eventId and associates the client to the event
	 */

	// FIXME: should eventid be an integer? think through
	public String registerClient(String eventId) {

		// XXX:think later on how to create without many steps!
		// new RabbitMQClient(clientName, description);
		this.clientId = Util.getUUID();
		// To register, for now add to a map? later move this to a datastore

		new DataStore().addRegistration(this.clientId.toString(), eventId.toString());
		// this needs to be stored later to fetch for registration
		new DataStore().addClient(this.clientId.toString(), this.clientName);
		// return a generated exchange id
		// return (this.clientId.toString() + string).replace("-", "");
		return (getClientName(clientId.toString()) + "." + eventId);

	}

	// Future method to be refactored when we will have API's
	public String registerClient(String clientId, String eventId) {

		// XXX:think later on how to create without many steps!
		// new RabbitMQClient(clientName, description);;
		this.clientId = UUID.fromString(clientId);

		// To register, for now add to a map? later move this to a datastore

		DataStore d = new DataStore();
		d.addRegistration(this.clientId.toString(), eventId);
		// return (this.clientId.toString() + eventId);
		return (getClientName(clientId) + "." + eventId);
	}

	private String getClientName(String clientId) {

		/*
		 * XXX:temporarily, get the name from the current object. this has to be modified to take it
		 * from the store
		 */

		return new DataStore().getClientName(clientId);

	}

	public void publish(MessagePublisher messagePublisher) {

		this.executorService.execute(new MessagePublisher(messagePublisher.getChannel(),
				messagePublisher.getExchangeId(), messagePublisher.getRoutingKey(),
				messagePublisher.getMessage()) {

		});

	}

	public void waitForScheduledTasksToComplete(int i, TimeUnit seconds)
			throws InterruptedByTimeoutException {

		// Wait until all threads are finish
		try {
			this.executorService.awaitTermination(200, TimeUnit.SECONDS);
		} catch (InterruptedException e) {

			throw new InterruptedByTimeoutException();
		}

	}
}