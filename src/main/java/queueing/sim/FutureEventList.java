package queueing.sim;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * A list of events sorted by their simulation clock and priority.
 *
 * <p>The imminent event is always the first event in the list. Events with lower simulation times
 * are considered imminent. If two events have the same simulation time, the event with the lower
 * priority value is considered imminent. If they also have the same priority, their insertion order
 * is preserved.
 */
final class FutureEventList {

  /** The events currently scheduled in the Future Event List. */
  private final List<Event> events = new ArrayList<>();

  /**
   * The comparator used to order events in the Future Event List.
   *
   * <p>Events with lower simulation times are ordered first. For events with the same simulation
   * time, events with lower priority values are ordered first.
   */
  private final Comparator<Event> comparator = Comparator.comparingDouble(Event::clock)
      .thenComparingInt(Event::priority);

  /**
   * Constructs a Future Event List with the given events.
   *
   * @param events the events to initialize the Future Event List with
   */
  FutureEventList(Event... events) {
    for (Event event : events) {
      this.events.add(Objects.requireNonNull(event, "event cannot be null"));
    }
  }

  /**
   * Inserts an event into the Future Event List.
   *
   * @param event the event to insert
   * @throws NullPointerException if {@code event} is {@code null}
   */
  void insert(Event event) {
    this.events.add(Objects.requireNonNull(event, "event cannot be null"));
  }

  /**
   * Returns the imminent event without removing it from the Future Event List.
   *
   * @return the imminent event
   * @throws IllegalStateException if the Future Event List is empty
   */
  Event peek() {
    this.ensureNotEmpty();
    this.events.sort(this.comparator);
    return this.events.get(0);
  }

  /**
   * Removes and returns the imminent event from the Future Event List.
   *
   * <p>If multiple events have the same simulation time, the event with the lowest priority value
   * is considered imminent. If they also have the same priority, the event inserted first is
   * considered imminent.
   *
   * @return the imminent event
   * @throws IllegalStateException if the Future Event List is empty
   */
  Event imminent() {
    this.ensureNotEmpty();
    this.events.sort(this.comparator);
    return this.events.removeFirst();
  }

  /**
   * Returns whether the Future Event List contains no scheduled events.
   *
   * @return {@code true} if the Future Event List is empty, otherwise {@code false}
   */
  boolean isEmpty() {
    return this.events.isEmpty();
  }

  @Override
  public String toString() {
    return this.events.toString();
  }

  /** Ensures that the Future Event List contains at least one event. */
  private void ensureNotEmpty() {
    if (this.events.isEmpty()) {
      throw new IllegalStateException("Future Event List is empty");
    }
  }
}
