package queueing.sim;

import java.util.List;
import java.util.Objects;

/**
 * Represents the arrival of an entity to a queueing system.
 *
 * <p>When executed, the event looks for available servers across the entire queueing system. If a
 * server is available, the server selection strategy determines which server receives the arriving
 * entity. Otherwise, the arrival queue selection strategy determines which queue receives the
 * entity.
 *
 * <p>The event always schedules the next arrival.
 */
final class Arrival implements Event {

  /** The simulation time at which this arrival occurs. */
  private final double clock;

  /** The entity arriving to the system. */
  private final Entity entity;

  /** Generates the time until the next arrival. */
  private final TimeGenerator interarrivalTimeGenerator;

  /** Generates the next entity that will arrive. */
  private final EntityGenerator entityGenerator;

  /**
   * Creates an arrival event.
   *
   * @param clock the simulation time at which the arrival occurs
   * @param entity the entity arriving to the system
   * @param interarrivalTimeGenerator the generator used to determine the next interarrival time
   * @param entityGenerator the generator used to create the next entity
   * @throws NullPointerException if any object parameter is {@code null}
   */
  Arrival(
      double clock,
      Entity entity,
      TimeGenerator interarrivalTimeGenerator,
      EntityGenerator entityGenerator) {
    this.clock = clock;
    this.entity = Objects.requireNonNull(entity);
    this.interarrivalTimeGenerator = Objects.requireNonNull(interarrivalTimeGenerator);
    this.entityGenerator = Objects.requireNonNull(entityGenerator);
  }

  @Override
  public double clock() {
    return this.clock;
  }

  @Override
  public int priority() {
    return 1;   // Mientras mas alto el numero, menor prioridad
  }

  /**
   * Executes the arrival.
   *
   * <p>If at least one server is available, the server selection strategy determines which server
   * receives the arriving entity. The selected server becomes busy, starts serving the entity, and
   * a departure event is scheduled.
   *
   * <p>If no server is available, the arrival queue selection strategy determines which queue
   * receives the entity.
   *
   * <p>The next arrival is always scheduled.
   *
   * @param context the simulation context
   */
  @Override
  public void execute(SimulationContext context) {

    
    InternalSimulationContext internalContext = (InternalSimulationContext) context;
    List<ServerView> availableServers = internalContext.model().availableServers();

    // Colocar el Probe para calcular o obtener la cantidad de arribos
    internalContext.probes().forEach(probe -> probe.entityArrived(this.clock, this.entity));

    if (!availableServers.isEmpty()) {
      // Devuelve un entero que representa el id del servidor
      int selectedServerId = internalContext.strategyBag().serverSelection().select(availableServers);
      Server selectedServer = internalContext.model().server(selectedServerId);
      
      selectedServer.seize(this.entity);

      // Probe notifica que el servidor seleccionado se ha ocupado
      internalContext.probes().forEach(probe -> probe.serviceStarted(this.clock, this.entity, selectedServer));
      internalContext.probes().forEach(probe -> probe.serverBecameBusy(this.clock, selectedServer));

      double serviceDuration = selectedServer.serviceDuration(internalContext.randomGenerator());
      
      internalContext.schedule(
          new Departure(
              this.clock + serviceDuration, this.entity, selectedServer
          )
      );
    }
    else {
      List<QueueView> queues = internalContext.model().queues();
      int selectedQueue = internalContext.strategyBag().arrivalQueueSelection().select(queues);
      Queue queue = internalContext.model().queue(selectedQueue);
      queue.add(this.entity);

      // Probe notifica que la entidad ha sido encolada
      // Checkearlo
      internalContext.probes().forEach(probe -> probe.entityEnteredQueue(this.clock, this.entity, queue));
      internalContext.probes().forEach(probe -> probe.queueSizeChanged(this.clock, queue));
    }
    
    internalContext.schedule(
        new Arrival(
            this.clock + interarrivalTimeGenerator.generate(internalContext.randomGenerator()),
            this.entityGenerator.generate(),
            this.interarrivalTimeGenerator,
            this.entityGenerator
        )
    );
  }

  @Override
  public String toString() {
    return "Arrival[clock=" + this.clock + ", entity=" + this.entity + "]";
  }
}
