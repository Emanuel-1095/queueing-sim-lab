package queueing.sim.probes;

import java.util.HashMap;
import java.util.Map;

import queueing.sim.EntityView;
import queueing.sim.QueueView;
import queueing.sim.ServerView;

/**
 * A probe that records the time entities spend waiting in queues.
 *
 * <p>The waiting time is measured from the moment an entity enters a queue until the moment it
 * starts receiving service.
 *
 * <p>The probe stores the queue entry time of each waiting entity. Once the entity starts service,
 * its waiting time is calculated and recorded, and its queue entry time is removed from the
 * internal map.
 */
public final class WaitingTime implements Probe {

  // Nuestro "anotador": guarda [ID de Entidad -> Tiempo en que entró a la cola]
  private final Map<Long, Double> entryTimes = new HashMap<>();
  // El acumulador total del tiempo que esperaron todos los clientes
  private double totalWaitingTime = 0.0;

  /**
   * Records the time at which an entity enters a queue.
   *
   * @param clock the simulation time at which the entity entered the queue
   * @param entity the entity that entered the queue
   * @param queue the queue entered by the entity
   */
  @Override
  public void entityEnteredQueue(double clock, EntityView entity, QueueView queue) {
    this.entryTimes.put(entity.id(), clock);
  }

  /**
   * Records the waiting time of an entity when it starts service.
   *
   * @param clock the simulation time at which the entity started service
   * @param entity the entity that started service
   * @param server the server that started serving the entity
   */
  @Override
  public void serviceStarted(double clock, EntityView entity, ServerView server) {
    Double entryTime = this.entryTimes.remove(entity.id());
    if (entryTime != null) {
      double waitingTime = clock - entryTime;
      this.totalWaitingTime += waitingTime;
    }
  }

  /**
   * Returns the total waiting time.
   *
   * @return the total waiting time
   */
  public double totalWaitingTime() {
    return this.totalWaitingTime;
  }
}
