package queueing.sim;

import java.util.Objects;
import java.util.random.RandomGenerator;

/**
 * Represents a server in the queueing system.
 *
 * <p>A server can serve at most one entity at a time. A server is busy when it has an entity
 * assigned to it; otherwise, it is idle.
 */
final class Server implements ServerView {

  /** The identifier of this server. */
  private final int id;

  /** The generator used to determine service times for entities assigned to this server. */
  private final TimeGenerator serviceTimeGenerator;

  /** The entity currently being served, or {@code null} if the server is idle. */
  private Entity entity;

  /**
   * Creates a server.
   *
   * @param id the identifier of the server
   * @param serviceTimeGenerator the generator used to determine service times
   * @throws NullPointerException if {@code serviceTimeGenerator} is {@code null}
   */
  Server(int id, TimeGenerator serviceTimeGenerator) {
    this.id = id;
    this.serviceTimeGenerator = Objects.requireNonNull(serviceTimeGenerator);
  }

  /**
   * Returns the identifier of this server.
   *
   * @return the server identifier
   */
  @Override
  public int id() {
    return this.id;
  }

  /**
   * Returns whether this server is currently busy.
   *
   * @return {@code true} if the server is serving an entity, otherwise {@code false}
   */
  @Override
  public boolean isBusy() {
    return this.entity != null;
  }

  /**
   * Generates a service duration for this server.
   *
   * @param randomGenerator the source of randomness
   * @return the generated service duration
   */
  double serviceDuration(RandomGenerator randomGenerator) {
    return this.serviceTimeGenerator.generate(randomGenerator);
  }

  /**
   * Returns the entity currently being served.
   *
   * @return the entity being served, or {@code null} if the server is idle
   */
  Entity entity() {
    return this.entity;
  }

  /**
   * Seizes this server for the given entity, making the server busy.
   *
   * @param entity the entity to serve
   * @throws NullPointerException if {@code entity} is {@code null}
   * @throws IllegalStateException if the server is already busy
   */
  void seize(Entity entity) {
    Objects.requireNonNull(entity);

    if (this.isBusy()) {
      throw new IllegalStateException("Cannot seize the server because the server is busy");
    }

    this.entity = entity;
  }

  /**
   * Releases the server from the entity currently being served.
   *
   * @return the entity that was being served
   * @throws IllegalStateException if the server is idle
   */
  Entity release() {
    if (!this.isBusy()) {
      throw new IllegalStateException("Cannot release the server because the server is idle");
    }

    Entity entity = this.entity;
    this.entity = null;

    return entity;
  }

  @Override
  public String toString() {
    if (this.isBusy()) {
      return "Server[id=" + this.id + ", state=BUSY, entity=" + this.entity + "]";
    }

    return "Server[id=" + this.id + ", state=IDLE]";
  }
}
