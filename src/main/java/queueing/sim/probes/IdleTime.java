package queueing.sim.probes;

import java.util.HashMap;
import java.util.Map;

import queueing.sim.ServerView;

/**
 * A probe that records the periods during which servers remain idle.
 *
 * <p>An idle period begins when a server becomes idle and ends when the server becomes busy again.
 *
 * <p>All servers are expected to be reported as idle when the simulation starts. Any idle period
 * that remains open when the simulation ends is closed using the simulation end time.
 */
public final class IdleTime implements Probe {

  private final Map<Integer, Double> totalIdleTime = new HashMap<>();
  private final Map<Integer, Double> idleStartTimes = new HashMap<>();

  /**
   * Records the moment at which a server becomes idle.
   *
   * @param clock the simulation time at which the server became idle
   * @param server the server that became idle
   */
  @Override
  public void serverBecameIdle(double clock, ServerView server) {
    idleStartTimes.put(server.id(), clock);
  }

  /**
   * Records the idle time of a server when it becomes busy.
   *
   * <p>A server becomes busy when it transitions from an idle state to a busy state. Starting
   * service for an entity does not necessarily mean that the server became busy, since a server may
   * already be busy when it starts serving another entity from a queue.
   *
   * @param clock the simulation time at which the server became busy
   * @param server the server that became busy
   */
  @Override
  public void serverBecameBusy(double clock, ServerView server) {
    if (!idleStartTimes.containsKey(server.id())) {
      throw new IllegalStateException("Server has not been reported as idle: " + server);
    }
    double idleStartTime = idleStartTimes.remove(server.id());
    double idleTime = clock - idleStartTime;
    totalIdleTime.put(server.id(), totalIdleTime.getOrDefault(server.id(), 0.0) + idleTime);
  }

  /**
   * Closes all idle periods that remain open when the simulation ends.
   *
   * @param clock the simulation time at which the simulation ended
   */
  @Override
  public void simulationFinished(double clock) {
    for (Map.Entry<Integer, Double> entry : idleStartTimes.entrySet()) {
      double idleStartTime = entry.getValue();
      double idleTime = clock - idleStartTime;
      totalIdleTime.put(entry.getKey(), totalIdleTime.getOrDefault(entry.getKey(), 0.0) + idleTime);
    }
    idleStartTimes.clear();
  }

  /**
   * Returns the total idle time for the specified server.
   *
   * @param serverId the identifier of the server
   * @return the total idle time for the server
   * @throws IllegalArgumentException if no idle time was recorded for the server
   */
  public double totalIdleTime(int serverId) {
    Double total = this.totalIdleTime.get(serverId);
    if (total == null) {
      throw new IllegalArgumentException("No idle time recorded for server: " + serverId);
    }
    return total;
  }
}
