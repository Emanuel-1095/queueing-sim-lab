package queueing.sim.probes;

import java.util.HashMap;
import java.util.Map;

import queueing.sim.EntityView;
import queueing.sim.ServerView;
import random.variates.Normal;

/**
 * A probe that records the remaining soil durability of each server (runway).
 *
 * <p>Each server starts with an initial durability of {@code 3000.0} units. Every time an entity
 * starts service on a server, the server's durability is reduced by a random sample drawn from a
 * normal distribution with {@code mean = 5.0} and {@code stdDev = 1.0}.
 */
public final class RunwayDurability implements Probe {

  private static final double INITIAL_DURABILITY = 3000.0;

  private final Map<Integer, Double> durabilityByServer = new HashMap<>();
  private final Normal wearDistribution = new Normal(5.0, 1.0);

  /**
   * Initializes the durability of a server when it is first reported as idle at the start of the
   * simulation.
   *
   * @param clock the simulation time at which the server became idle
   * @param server the server that became idle
   */
  @Override
  public void serverBecameIdle(double clock, ServerView server) {
    durabilityByServer.putIfAbsent(server.id(), INITIAL_DURABILITY);
  }

  /**
   * Applies runway wear to the server when an entity starts receiving service (landing).
   *
   * @param clock the simulation time at which service started
   * @param entity the entity receiving service
   * @param server the server providing the service
   */
  @Override
  public void serviceStarted(double clock, EntityView entity, ServerView server) {
    double currentDurability =
        durabilityByServer.getOrDefault(server.id(), INITIAL_DURABILITY);
    double wear = Math.max(0.0, wearDistribution.sample());
    durabilityByServer.put(server.id(), currentDurability - wear);
  }

  /**
   * Returns the remaining durability recorded for the specified server.
   *
   * @param serverId the identifier of the server
   * @return the remaining durability for the server
   * @throws IllegalArgumentException if no durability has been recorded for the specified server
   */
  public double durability(int serverId) {
    if (!durabilityByServer.containsKey(serverId)) {
      throw new IllegalArgumentException(
          "No durability recorded for server: " + serverId);
    }
    return durabilityByServer.get(serverId);
  }
}

