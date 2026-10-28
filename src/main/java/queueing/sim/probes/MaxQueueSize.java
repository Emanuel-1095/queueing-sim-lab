package queueing.sim.probes;

import java.util.HashMap;
import java.util.Map;

import queueing.sim.QueueView;

/**
 * A probe that records the maximum size reached by each queue.
 *
 * <p>The maximum size is updated whenever a queue reports a size change. The probe stores the
 * maximum size independently for each queue.
 */
public final class MaxQueueSize implements Probe {

  private final Map<Integer, Integer> maxSizeQueues = new HashMap<>();
  /**
   * Records the current size of a queue.
   *
   * <p>If the current size is greater than the previously recorded maximum, the maximum is updated.
   *
   * @param clock the simulation time at which the queue size changed
   * @param queue the queue whose size changed
   */
  @Override
  public void queueSizeChanged(double clock, QueueView queue) {
    Integer maxSize = maxSizeQueues.get(queue.id());
    Integer currentSize = queue.size();
    if (maxSize == null || currentSize > maxSize) {
      maxSizeQueues.put(queue.id(), currentSize);
    }
  }

  /**
   * Returns the maximum size recorded for the specified queue.
   *
   * @param queueId the identifier of the queue
   * @return the maximum size reached by the queue
   * @throws IllegalArgumentException if no size change has been recorded for the specified queue
   */
  public int maxQueueSize(int queueId) {
    if (!maxSizeQueues.containsKey(queueId)) {
      throw new IllegalArgumentException(
          "No size change recorded for queue with ID: " + queueId);
    }
    return maxSizeQueues.get(queueId);
  }
}
