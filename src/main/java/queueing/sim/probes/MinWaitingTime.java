package queueing.sim.probes;

import java.util.HashMap;
import java.util.Map;

import queueing.sim.EntityView;
import queueing.sim.QueueView;
import queueing.sim.ServerView;

/**
 * A probe that records the minimum non-zero time an entity spends waiting in a queue.
 *
 * <p>The waiting time is measured from the moment an entity enters a queue until the moment it
 * starts receiving service.
 *
 * <p>The probe stores the queue entry time of each waiting entity. Once the entity starts service,
 * its waiting time is calculated and compared with the current minimum.
 */
public final class MinWaitingTime implements Probe {

  private final Map<Long, Double> entryTimes = new HashMap<>();
  private double minWaitingTime = Double.POSITIVE_INFINITY;

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
   * <p>If the calculated waiting time is strictly greater than zero and less than the current
   * minimum, the minimum is updated.
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
    if (entryTime != null) {
      double waitingTime = clock - entryTime;
      if (waitingTime > 0.0 && waitingTime < minWaitingTime) {
        minWaitingTime = waitingTime;
      }
    }
  }

  /**
   * Returns the minimum non-zero waiting time recorded.
   *
   * @return the minimum waiting time, or {@code 0.0} if no positive waiting time was recorded
   */
  public double minWaitingTime() {
    return Double.isInfinite(minWaitingTime) ? 0.0 : minWaitingTime;
  }
}

