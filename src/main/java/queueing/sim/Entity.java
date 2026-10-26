package queueing.sim;

/**
 * Represents an entity that participates in a queueing simulation.
 *
 * <p>An entity is uniquely identified by its identifier.
 */
final class Entity implements EntityView {

  /** The unique identifier of this entity. */
  private final long id;

  /** The arrival time of this entity. */
  private double arrivalTime;

  /**
   * Creates an entity.
   *
   * @param id the identifier of this entity
   */
  Entity(long id) {
    this.id = id;
  }

  @Override
  public long id() {
    return this.id;
  }

  /**
   * Returns the arrival time of this entity.
   *
   * @return the entity arrival time
   */
  double arrivalTime() {
    return this.arrivalTime;
  }

  /**
   * Sets the arrival time of this entity.
   *
   * @param arrivalTime the entity arrival time
   */
  void arrivalTime(double arrivalTime) {
    this.arrivalTime = arrivalTime;
  }

  /**
   * Returns a string representation of this entity.
   *
   * @return a string containing the entity identifier
   */
  @Override
  public String toString() {
    return "Entity[id=" + this.id + "]";
  }
}
