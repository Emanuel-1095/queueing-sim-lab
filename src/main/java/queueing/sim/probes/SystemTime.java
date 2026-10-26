package queueing.sim.probes;

import java.util.HashMap;
import java.util.Map;

import queueing.sim.EntityView;

/**
 * A probe that records the time spent in the system by entities.
 *
 * <p>The time in system is measured from the moment an entity arrives until the moment it departs.
 *
 * <p>The probe stores the arrival time of each entity until that entity departs. Once the entity
 * departs, its time in system is calculated and recorded, and its arrival time is removed from the
 * internal map.
 */
public final class SystemTime implements Probe {

  private double totalSystemTime = 0.0;
  private final Map<EntityView, Double> arrivalTimes = new HashMap<>();

  /**
   * Records the arrival time of an entity.
   *
   * @param clock the simulation time at which the entity arrived
   * @param entity the entity that arrived
   */
  @Override
  public void entityArrived(double clock, EntityView entity) {
    arrivalTimes.put(entity, clock);
  }

  /**
   * Records the time spent in the system by an entity that departs.
   *
   * @param clock the simulation time at which the entity departed
   * @param entity the entity that departed
   */
  @Override
  public void entityDeparted(double clock, EntityView entity) {
    if (!arrivalTimes.containsKey(entity)) {
      throw new IllegalStateException("Entity has not arrived: " + entity);
    }
    double arrivalTime = arrivalTimes.remove(entity);
    double systemTime = clock - arrivalTime;
    totalSystemTime += systemTime;
  }

  /**
   * Returns the total time spent in the system by all entities that have departed.
   *
   * @return the total system time
   */
  public double totalSystemTime() {
    return this.totalSystemTime;
  }
}
