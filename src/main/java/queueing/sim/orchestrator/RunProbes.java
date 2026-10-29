package queueing.sim.orchestrator;
import java.util.List;
import queueing.sim.probes.*;
/**
 * Contenedor de las sondas concretas utilizadas durante una ejecución de simulación.
 * Permite acceder directamente a las métricas sin necesidad de casteos.
 */
public class RunProbes {
    public final ArrivalCounter arrivalCounter = new ArrivalCounter();
    public final ServedEntitiesCounter servedCounter = new ServedEntitiesCounter();
    public final DepartureCounter departureCounter = new DepartureCounter();
    public final WaitingTime waitingTime = new WaitingTime();
    public final MaxWaitingTime maxWaitingTime = new MaxWaitingTime();
    public final SystemTime systemTime = new SystemTime();
    public final MaxSystemTime maxSystemTime = new MaxSystemTime();
    public final IdleTime idleTime = new IdleTime();
    public final MaxIdleTime maxIdleTime = new MaxIdleTime();
    public final MaxQueueSize maxQueueSize = new MaxQueueSize();
    /**
     * Retorna todas las sondas como una lista para pasar al constructor de Simulation.
     */
    public List<Probe> toList() {
        return List.of(
            arrivalCounter,
            servedCounter,
            departureCounter,
            waitingTime,
            maxWaitingTime,
            systemTime,
            maxSystemTime,
            idleTime,
            maxIdleTime,
            maxQueueSize
        );
    }
}