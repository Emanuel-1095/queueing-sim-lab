package queueing.sim;

import java.util.List;
import java.util.Objects;
import java.util.random.RandomGenerator;
import queueing.sim.probes.Probe;
import queueing.sim.strategies.StrategyBag;

/**
 * Internal implementation of {@link SimulationContext}.
 *
 * <p>This class provides events with controlled access to the simulation state while keeping
 * internal implementation details hidden from the public API.
 */
final class InternalSimulationContext implements SimulationContext {

  /** The Future Event List used by the simulation. */
  private final FutureEventList futureEventList;

  /** The random generator used by the simulation. */
  private final RandomGenerator randomGenerator;

  /** The queueing model being simulated. */
  private final QueueingModel model;

  /** The strategy bag that contains strategies used by the simulation. */
  private final StrategyBag strategyBag;

  /** The list of probes to observe the simulation. */
  private final List<Probe> probes;

  /**
   * Creates an internal simulation context.
   *
   * @param futureEventList the Future Event List used by the simulation
   * @param randomGenerator the random generator used by the simulation
   * @param model the queueing model being simulated
   * @param strategyBag the strategy bag that contains strategies used by the simulation
   * @param probes the list of probes to observe the simulation
   * @throws NullPointerException if any argument is {@code null}
   */
  InternalSimulationContext(
      FutureEventList futureEventList,
      RandomGenerator randomGenerator,
      QueueingModel model,
      StrategyBag strategyBag,
      List<Probe> probes) {

    this.futureEventList = Objects.requireNonNull(futureEventList);
    this.randomGenerator = Objects.requireNonNull(randomGenerator);
    this.model = Objects.requireNonNull(model);
    this.strategyBag = Objects.requireNonNull(strategyBag);
    this.probes = Objects.requireNonNull(probes);
  }

  StrategyBag strategyBag() {
    return strategyBag;
    // throw new UnsupportedOperationException("Unimplemented method 'strategyBag'");
  }

  List<Probe> probes() {
    throw new UnsupportedOperationException("Unimplemented method 'probes'");
  }

  @Override
  public RandomGenerator randomGenerator() {
    throw new UnsupportedOperationException("Unimplemented method 'randomGenerator'");
  }

  @Override
  public QueueingModel model() {
    throw new UnsupportedOperationException("Unimplemented method 'model'");
  }

  @Override
  public void schedule(Event event) {
    this.futureEventList.insert(event);
    throw new UnsupportedOperationException("Unimplemented method 'schedule'");
  }
}
