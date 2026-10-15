package queueing.sim.probes;

import queueing.sim.EntityView;
import queueing.sim.QueueView;
import queueing.sim.ServerView;

/**
 * Observes occurrences during the execution of a simulation.
 *
 * <p>A probe can collect, calculate, or record information about a simulation without controlling
 * its execution or directly modifying its state.
 *
 * <p>All notification methods have default empty implementations. A probe only needs to override
 * the notifications that are relevant to it.
 */
public interface Probe {

  /**
   * Notifies that an entity has arrived to the system.
   *
   * @param clock the simulation time at which the entity arrived
   * @param entity the entity that arrived
   */
  default void entityArrived(double clock, EntityView entity) {}

  /**
   * Notifies that an entity has entered a queue.
   *
   * @param clock the simulation time at which the entity entered the queue
   * @param entity the entity that entered the queue
   * @param queue the queue entered by the entity
   */
  default void entityEnteredQueue(double clock, EntityView entity, QueueView queue) {}

  /**
   * Notifies that an entity has started receiving service.
   *
   * @param clock the simulation time at which service started
   * @param entity the entity receiving service
   * @param server the server providing the service
   */
  default void serviceStarted(double clock, EntityView entity, ServerView server) {}

  /**
   * Notifies that an entity has departed from the system.
   *
   * @param clock the simulation time at which the entity departed
   * @param entity the entity that departed
   */
  default void entityDeparted(double clock, EntityView entity) {}

  /**
   * Notifies that a server has become idle.
   *
   * @param clock the simulation time at which the server became idle
   * @param server the server that became idle
   */
  default void serverBecameIdle(double clock, ServerView server) {}

  /**
   * Notifies that a server has become busy.
   *
   * <p>This notification represents a transition of the server from an idle state to a busy state.
   *
   * @param clock the simulation time at which the server became busy
   * @param server the server that became busy
   */
  default void serverBecameBusy(double clock, ServerView server) {}

  /**
   * Notifies that the size of a queue has changed.
   *
   * @param clock the simulation time at which the queue size changed
   * @param queue the queue whose size changed
   */
  default void queueSizeChanged(double clock, QueueView queue) {}


// Corresponden a la simulación. Indican el inicio y el fin de la simulación

  /**
   * Notifies that the simulation has started.
   *
   * <p>This notification is sent once, before the simulation begins processing events.
   *
   * @param clock the simulation time at which the simulation started
   */
  default void simulationStarted(double clock) {}

  /**
   * Notifies that the simulation has finished.
   *
   * <p>This notification is sent once, after the simulation has finished processing events.
   *
   * @param clock the simulation time at which the simulation finished
   */
  default void simulationFinished(double clock) {}
}
