package queueing.sim;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Represents the queueing model simulated by a {@link Simulation}.
 *
 * <p>A queueing model consists of one or more queues. Every queue has at least one associated
 * server. Each server is associated with exactly one queue.
 *
 * <p>Queues and servers are created internally from the supplied {@link QueueConfig} objects.
 */

public final class QueueingModel {

  /** The queues that compose this model. */
  private final List<Queue> queues;

  /** The servers that compose this model. */
  private final List<Server> servers;

  /**
   * Creates a queueing model from the supplied queue configurations.
   *
   * <p>Each {@link QueueConfig} creates one queue. Each {@link ServerConfig} contained in a
   * configuration creates one server associated with that queue.
   *
   * @param queueConfigs the configurations of the queues that compose the model
   * @throws NullPointerException if {@code queueConfigs} is {@code null} or contains {@code null}
   *     elements
   * @throws IllegalArgumentException if {@code queueConfigs} is empty, contains duplicate queue
   *     identifiers, or contains duplicate server identifiers
   */
  public QueueingModel(List<QueueConfig> queueConfigs) {

    Objects.requireNonNull(queueConfigs);

    if (queueConfigs.isEmpty()) {
      throw new IllegalArgumentException("A queueing model must have at least one queue");
    }

    this.queues = createQueues(queueConfigs);

    this.servers = this.queues.stream().flatMap(queue -> queue.servers().stream()).toList();
  }

  /**
   * Returns the internal queue with the specified identifier.
   *
   * <p>This method is package-private because the internal {@link Queue} implementation must not be
   * exposed as part of the public API.
   *
   * @param id the identifier of the queue
   * @return the queue with the specified identifier
   * @throws IllegalArgumentException if no queue with the specified identifier exists
   */
  Queue queue(int id) {
    return this.queues.stream()
        .filter(queue -> queue.id() == id)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No queue exists with id: " + id));
  }

  /**
   * Returns the internal server with the specified identifier.
   *
   * <p>This method is package-private because the internal {@link Server} implementation must not
   * be exposed as part of the public API.
   *
   * @param id the identifier of the server
   * @return the server with the specified identifier
   * @throws IllegalArgumentException if no server with the specified identifier exists
   */
  Server server(int id) {
    return this.servers.stream()
        .filter(server -> server.id() == id)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No server exists with id: " + id));
  }

  /**
   * Returns a read-only view of all servers in the queueing model.
   *
   * @return the servers in the queueing model
   */
  List<ServerView> servers() {
    return List.copyOf(this.servers);
  }

  /**
   * Returns read-only views of the queues that compose this model.
   *
   * <p>The returned views expose only information that can safely be used by clients, such as the
   * queue identifier and the number of waiting entities.
   *
   * @return an unmodifiable list of queue views
   */
  public List<QueueView> queues() {
    return this.queues.stream().map(queue -> (QueueView) queue).toList();
  }

  /**
   * Returns read-only views of all currently available servers.
   *
   * <p>A server is considered available when it is not currently serving an entity.
   *
   * @return an unmodifiable list of available server views
   */
  public List<ServerView> availableServers() {
    return this.servers.stream()
        .filter(server -> !server.isBusy())
        .map(server -> (ServerView) server)
        .toList();
  }

  /**
   * Returns a string representation of the current state of this queueing model.
   *
   * <p>The representation includes all queues in their configured order.
   *
   * @return a string representation of this queueing model
   */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("QueueingModel[\n");

    for (int i = 0; i < this.queues.size(); i++) {
      builder.append("  ").append(this.queues.get(i));

      if (i < this.queues.size() - 1) {
        builder.append('\n');
      }
    }

    builder.append("\n]");

    return builder.toString();
  }

  /**
   * Creates the internal queues and servers from the supplied configurations.
   *
   * <p>Queue identifiers and server identifiers must be unique across the entire model.
   *
   * @param queueConfigs the queue configurations
   * @return the created queues
   * @throws NullPointerException if a queue configuration is {@code null} or a server configuration
   *     is {@code null}
   * @throws IllegalArgumentException if queue or server identifiers are duplicated
   */
  private static List<Queue> createQueues(List<QueueConfig> queueConfigs) {

    Set<Integer> queueIds = new HashSet<>();
    Set<Integer> serverIds = new HashSet<>();
    List<Queue> queues = new ArrayList<>();

    for (QueueConfig configuration : queueConfigs) {

      Objects.requireNonNull(configuration);

      if (!queueIds.add(configuration.id())) {
        throw new IllegalArgumentException("Queue identifiers must be unique");
      }

      List<Server> servers = new ArrayList<>();

      for (ServerConfig serverConfiguration : configuration.servers()) {

        Objects.requireNonNull(serverConfiguration);

        if (!serverIds.add(serverConfiguration.id())) {
          throw new IllegalArgumentException("Server identifiers must be unique");
        }

        servers.add(
            new Server(serverConfiguration.id(), serverConfiguration.serviceTimeGenerator()));
      }

      queues.add(new Queue(configuration.id(), servers));
    }

    return List.copyOf(queues);
  }
}
