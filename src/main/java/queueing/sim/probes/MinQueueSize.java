package queueing.sim.probes;

import java.util.HashMap;
import java.util.Map;

import queueing.sim.QueueView;

/**
 * A probe that records the minimum non-zero size reached by each queue.
 *
 * <p>The minimum size is updated whenever a queue reports a size change greater than zero. The
 * probe stores the minimum size independently for each queue.
 */
public final class MinQueueSize implements Probe {

  private final Map<Integer, Integer> minSizeQueues = new HashMap<>();

  /**
   * Records the current size of a queue.
   *
   * <p>If the current size is strictly greater than zero and less than the previously recorded
   * minimum, the minimum is updated.
   *
   * @param clock the simulation time at which the queue size changed
   * @param queue the queue whose size changed
   */
  @Override
  public void queueSizeChanged(double clock, QueueView queue) {
    Integer minSize = minSizeQueues.get(queue.id());
    Integer currentSize = queue.size();
    if (currentSize > 0 && (minSize == null || currentSize < minSize)) {
      minSizeQueues.put(queue.id(), currentSize);
    }
  }

  /**
   * Returns the minimum non-zero size recorded for the specified queue.
   *
   * @param queueId the identifier of the queue
   * @return the minimum non-zero size reached by the queue
   * @throws IllegalArgumentException if no non-zero size change has been recorded for the specified
   *     queue
   */
  public int minQueueSize(int queueId) {
    if (!minSizeQueues.containsKey(queueId)) {
      throw new IllegalArgumentException(
          "No size change recorded for queue with ID: " + queueId);
    }
    return minSizeQueues.get(queueId);
  }
}

