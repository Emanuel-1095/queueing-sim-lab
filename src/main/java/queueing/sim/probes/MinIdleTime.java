package queueing.sim.probes;

import java.util.HashMap;
import java.util.Map;

import queueing.sim.ServerView;

/**
 * A probe that records the minimum non-zero idle period for each server.
 *
 * <p>An idle period begins when a server becomes idle and ends when the server becomes busy again.
 *
 * <p>The probe stores the time at which each server became idle. When that server becomes busy
 * again, the elapsed idle time is calculated and compared with the current minimum for that server.
 *
 * <p>Any idle period that remains open when the simulation ends is closed using the simulation end
 * time.
 */
public final class MinIdleTime implements Probe {

  private final Map<Integer, Double> idleStartTimes = new HashMap<>();
  private final Map<Integer, Double> minIdleTimes = new HashMap<>();

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
    Double idleStartTime = idleStartTimes.remove(server.id());
    if (idleStartTime == null) {
      throw new IllegalStateException("Server has not become idle: " + server.id());
    }
    double idleDuration = clock - idleStartTime;
    if (idleDuration > 0.0) {
      Double currentMin = minIdleTimes.get(server.id());
      if (currentMin == null || idleDuration < currentMin) {
        minIdleTimes.put(server.id(), idleDuration);
      }
    }
  }

  /**
   * Closes all idle periods that remain open when the simulation ends.
   *
   * @param clock the simulation time at which the simulation ended
   */
  @Override
  public void simulationFinished(double clock) {
    for (Map.Entry<Integer, Double> entry : idleStartTimes.entrySet()) {
      double idleDuration = clock - entry.getValue();
      if (idleDuration > 0.0) {
        Double currentMin = minIdleTimes.get(entry.getKey());
        if (currentMin == null || idleDuration < currentMin) {
          minIdleTimes.put(entry.getKey(), idleDuration);
        }
      }
    }
    idleStartTimes.clear();
  }

  /**
   * Returns the minimum non-zero idle time recorded for the specified server.
   *
   * @param serverId the identifier of the server
   * @return the minimum idle time for the server
   * @throws IllegalArgumentException if no positive idle period has been recorded for the specified
   *     server
   */
  public double minIdleTime(int serverId) {
    if (!minIdleTimes.containsKey(serverId)) {
      throw new IllegalArgumentException("No idle period recorded for server: " + serverId);
    }
    return this.minIdleTimes.get(serverId);
  }
}

