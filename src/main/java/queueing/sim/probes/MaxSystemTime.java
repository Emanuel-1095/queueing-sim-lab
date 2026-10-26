package queueing.sim.probes;

import java.util.HashMap;
import java.util.Map;

import queueing.sim.EntityView;

/**
 * A probe that records the maximum time spent in the system by an entity.
 *
 * <p>The time in system is measured from the moment an entity arrives until the moment it departs.
 *
 * <p>The probe stores the arrival time of each entity until that entity departs. Once the entity
 * departs, its time in system is calculated and compared with the current maximum.
 */
public final class MaxSystemTime implements Probe {

  private double maxSystemTime = 0.0;
  private final Map<Long, Double> arrivalTimes = new HashMap<>();
  /**
   * Records the arrival time of an entity.
   *
   * @param clock the simulation time at which the entity arrived
   * @param entity the entity that arrived
   */
  @Override
  public void entityArrived(double clock, EntityView entity) {
    arrivalTimes.put(entity.id(), clock);
  }

  /**
   * Records the time spent in the system by an entity that departs.
   *
   * <p>If the calculated time in system is greater than the current maximum, the maximum is
   * updated.
   *
   * @param clock the simulation time at which the entity departed
   * @param entity the entity that departed
   */
  @Override
  public void entityDeparted(double clock, EntityView entity) {
    if (!arrivalTimes.containsKey(entity.id())) {
      throw new IllegalStateException("Entity has not arrived: " + entity);
    }
    double arrivalTime = arrivalTimes.remove(entity.id());
    double systemTime = clock - arrivalTime;
    if (systemTime > maxSystemTime) {
      maxSystemTime = systemTime;
    }
  }

  /**
   * Returns the maximum time spent in the system by an entity that has departed.
   *
   * @return the maximum system time
   */
  public double maxSystemTime() {
    return maxSystemTime;
  }
}
