package queueing.sim.orchestrator;

import queueing.sim.TimeGenerator;
import queueing.sim.strategies.StrategyBag;

public record SimulationConfig(
    double initClock,                       // Inicio de reloj de la simulación
    double simLength,                       // Longitud de la Simulación
    int numQueues,                          // Cantidad de pistas / colas
    int serversPerQueue,                    // Cantidad de servidores por cola
    TimeGenerator interarrivalGenerator,
    TimeGenerator serviceGenerator,
    StrategyBag strategyBag,
    int replications,                       // Cantidad de ejecuciones de una tirada (1 o N)
    boolean stepByStep
) {}