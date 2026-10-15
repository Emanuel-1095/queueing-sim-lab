package queueing.sim;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.random.RandomGenerator;
import queueing.sim.probes.Probe;
import queueing.sim.strategies.StrategyBag;

/**
 * Executes a discrete-event simulation of a queueing model.
 *
 * <p>The simulation processes events from the Future Event List in chronological order until the
 * simulation reaches its configured end time or there are no more events to process.
 */
public final class Simulation {

  /** The initial simulation clock. */
  private final double initClock;

  /** The simulation duration. */
  private final double simLength;

  /** The queueing model being simulated. */
  // Forma de definir el modelo de colas que se va a simular, incluyendo servidores, colas y entidades.
  // Es una capa de abstracción que encapsula la lógica del sistema de colas y permite que la simulación interactúe con él sin preocuparse por los detalles internos.
  // 
  private final QueueingModel model;      

  /** The random generator used by the simulation. */
  private final RandomGenerator randomGenerator;

  /** The strategies used by the simulation. */
  private final StrategyBag strategyBag;

  /** The generator of interarrival times. */
  private final TimeGenerator interarrivalTimeGenerator;  // Distinto al tiempo de servicio, este es el tiempo entre llegadas de entidades al sistema.

  /** The Future Event List containing scheduled events. */
  private final FutureEventList futureEventList;

  /** Whether the simulation waits for ENTER between events. */
  private final boolean stepByStep;

  /** The list of probes to observe the simulation. */
  private final List<Probe> probes;

  /**
   * Creates a simulation.
   *
   * @param initClock the initial simulation clock
   * @param simLength the simulation duration
   * @param model the queueing model
   * @param randomGenerator the random generator used by the simulation
   * @param interarrivalTimeGenerator the generator of interarrival times
   * @param strategyBag the strategies used by the simulation
   * @param probes the list of probes to observe the simulation
   * @param stepByStep whether the simulation should wait for ENTER between events
   * @throws NullPointerException if {@code model}, {@code randomGenerator}, {@code
   *     interarrivalTimeGenerator}, or {@code strategyBag} is {@code null}
   * @throws IllegalArgumentException if {@code simLength} is negative or not finite
   */
  public Simulation(
      double initClock,
      double simLength,
      QueueingModel model,
      RandomGenerator randomGenerator,
      TimeGenerator interarrivalTimeGenerator,
      StrategyBag strategyBag,
      List<Probe> probes,
      boolean stepByStep) {

    if (!Double.isFinite(simLength) || simLength < 0.0) {
      throw new IllegalArgumentException("Simulation length must be finite and non-negative");
    }

    throw new UnsupportedOperationException("Unimplemented constructor 'Simulation'");
  }

  /**
   * Schedules an event to occur during the simulation.
   *
   * @param event the event to schedule
   * @throws NullPointerException if {@code event} is {@code null}
   */
  public void schedule(Event event) {
    this.futureEventList.insert(Objects.requireNonNull(event));
  }

  /** Runs the simulation. */
  public void run() {

    double endClock = this.initClock + this.simLength;

    InternalSimulationContext context = null;

    EntityGenerator entityGenerator;

    Scanner scanner = this.stepByStep ? new Scanner(System.in) : null;

    // Code here: the probes must be notified that the simulation has started and that the servers
    // are currently idle.

    while (!this.futureEventList.isEmpty() && this.futureEventList.peek().clock() <= endClock) {

      if (this.stepByStep) {
        System.out.println("Step-by-step mode: Press ENTER to inspect the imminent event...");
        scanner.nextLine();
      }

      Event event = this.futureEventList.peek();

      if (this.stepByStep) {
        System.out.println("========================================");
        System.out.println("CLOCK: " + event.clock());
        System.out.println();
        System.out.println("EVENT:");
        System.out.println("  " + event);
        System.out.println();
        System.out.println("FEL:");
        System.out.println("  " + this.futureEventList);
        System.out.println();
        System.out.println("MODEL:");
        System.out.println(this.model);
        System.out.println("========================================\n");

        System.out.println("Press ENTER to process the imminent event...");
        scanner.nextLine();
      }

      // Code here: you must advance time in the simulation.

      if (this.stepByStep) {
        System.out.println("========================================");
        System.out.println();
        System.out.println("FEL:");
        System.out.println("  " + this.futureEventList);
        System.out.println();
        System.out.println("MODEL:");
        System.out.println(this.model);
        System.out.println("========================================\n");
      }
    }

    // Code here: notify the probes that the simulation has ended
  }
}
