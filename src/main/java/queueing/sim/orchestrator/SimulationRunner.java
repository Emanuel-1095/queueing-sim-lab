package queueing.sim.orchestrator;

import queueing.sim.*;
import queueing.sim.probes.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;

enum ProbesEnumeration{
    ARRIBAL_COUNTER,
    DEPARTURE_COUNTER,
    WAITING_TIME,
    MAX_WAITING_TIME,
    SYSTEM_TIME,
    MAX_SYSTEM_TIME,
    IDLE_TIME,
    MAX_IDLE_TIME,
    MAX_QUEUE_SIZE
}

public class SimulationRunner {

    public void run(SimulationConfig simulationConfig){

        Map<ProbesEnumeration, List<Probe>> acumulationProbes = new HashMap<>();

        for (int i = 0; i < simulationConfig.replications(); i++){

            List<ServerConfig> serverConfigList = new ArrayList<>();
            List<QueueConfig> queueConfigList = new ArrayList<>();

            for (int j = 0; j < simulationConfig.numQueues(); j++){
                
                for(int k = 0; k < simulationConfig.serversPerQueue(); k++){
                    ServerConfig serverConfig = new ServerConfig(k + (j * simulationConfig.serversPerQueue()), simulationConfig.serviceGenerator());
                    serverConfigList.add(serverConfig);
                }
                
                QueueConfig queueConfig = new QueueConfig(j, serverConfigList);
                queueConfigList.add(queueConfig);
            
            }

            QueueingModel queueingModel = new QueueingModel(queueConfigList);

            
            List<Probe> simulationProbes = new ArrayList<>();

            simulationProbes.add(new ArrivalCounter());
            simulationProbes.add(new DepartureCounter());
            simulationProbes.add(new WaitingTime());
            simulationProbes.add(new MaxWaitingTime());
            simulationProbes.add(new SystemTime());
            simulationProbes.add(new MaxSystemTime());
            simulationProbes.add(new IdleTime());
            simulationProbes.add(new MaxIdleTime());
            simulationProbes.add(new MaxQueueSize());

            // Checkear esta bullshit
            RandomGenerator randomGenerator = RandomGenerator.getDefault();

            Simulation simulation = new Simulation(
                simulationConfig.initClock(), simulationConfig.simLength(), queueingModel, 
                randomGenerator, simulationConfig.interarrivalGenerator(), 
                simulationConfig.strategyBag(), simulationProbes, simulationConfig.stepByStep()
            );
            
            simulation.run();

            acumulationProbes.put(simulationProbes);
        }

        if(simulationConfig.replications() > 1){
            // Calculo de los promedios de la cantidad de ejecuciones/replicaciones
            
            for(List<Probe> probes : acumulationProbes){
                


                probes.forEach(probeList -> probeList.forEach(probe -> probe.));
                
            }

        } else{
            // Muestra de los resultados de una ejecución/replicación


        }

    }

}
