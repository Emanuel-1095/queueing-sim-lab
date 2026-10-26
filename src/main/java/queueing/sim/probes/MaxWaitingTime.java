package queueing.sim.probes;

import java.util.HashMap;
import java.util.Map;

import queueing.sim.EntityView;
import queueing.sim.QueueView;
import queueing.sim.ServerView;

/**
 * A probe that records the maximum time an entity spends waiting in a queue.
 *
 * <p>The waiting time is measured from the moment an entity enters a queue until the moment it
 * starts receiving service.
 *
 * <p>The probe stores the queue entry time of each waiting entity. Once the entity starts service,
 * its waiting time is calculated and compared with the current maximum.
 */
public final class MaxWaitingTime implements Probe {

  private final Map<Long, Double> entryTimes = new HashMap<>();
  private double maxWaitingTime = 0.0;

  /**
   * Records the time at which an entity enters a queue.
   *
   * @param clock the simulation time at which the entity entered the queue
   * @param entity the entity that entered the queue
   * @param queue the queue entered by the entity
   */
  @Override
  public void entityEnteredQueue(double clock, EntityView entity, QueueView queue) {
    entryTimes.put(entity.id(), clock);
  }

  /**
   * Records the waiting time of an entity when it starts service.
   *
   * <p>If the calculated waiting time is greater than the current maximum, the maximum is updated.
   *
   * <p>If the entity did not enter a queue, no waiting time is recorded.
   *
   * @param clock the simulation time at which service started
   * @param entity the entity that started service
   * @param server the server that started serving the entity
   */
  @Override
  public void serviceStarted(double clock, EntityView entity, ServerView server) {
    Double entryTime = entryTimes.remove(entity.id());
    if (entryTime != null){
      double waitingTime = clock - entryTime;
      if (waitingTime > maxWaitingTime) {
        maxWaitingTime = waitingTime;
      }
    }
  }

  /**
   * Returns the maximum waiting time recorded.
   *
   * @return the maximum waiting time
   */
  public double maxWaitingTime() {
    return maxWaitingTime;
  }
}
