package queueing.sim.orchestrator;

import queueing.sim.*;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

public class SimulationRunner {

    public void run(SimulationConfig simulationConfig){

        List<RunProbes> allReplications = new ArrayList<>();
        RandomGenerator randomGenerator = RandomGenerator.getDefault();

        for (int i = 0; i < simulationConfig.replications(); i++){

            List<QueueConfig> queueConfigList = new ArrayList<>();
            
            for (int j = 0; j < simulationConfig.numQueues(); j++){

                List<ServerConfig> serverConfigList = new ArrayList<>();
                
                for(int k = 0; k < simulationConfig.serversPerQueue(); k++){
                    ServerConfig serverConfig = new ServerConfig(k + (j * simulationConfig.serversPerQueue()), simulationConfig.serviceGenerator());
                    serverConfigList.add(serverConfig);
                }
                
                QueueConfig queueConfig = new QueueConfig(j, serverConfigList);
                queueConfigList.add(queueConfig);
            
            }

            QueueingModel queueingModel = new QueueingModel(queueConfigList);
           
            RunProbes simulationProbes = new RunProbes();

            Simulation simulation = new Simulation(
                simulationConfig.initClock(), simulationConfig.simLength(), queueingModel, 
                randomGenerator, simulationConfig.interarrivalGenerator(), 
                simulationConfig.strategyBag(), simulationProbes.toList(), simulationConfig.stepByStep()
            );
            
            simulation.run();

            allReplications.add(simulationProbes);
        }

        if(simulationConfig.replications() > 1){
            showAvarageResults(allReplications, simulationConfig);
        } else{
            showIndividualResult(allReplications.getFirst(), simulationConfig);
        }

    }

    private void showIndividualResult(RunProbes p, SimulationConfig config) {
        System.out.println("==========================================================");
        System.out.println("         RESULTADOS DE LA SIMULACIÓN (1 corrida)");
        System.out.println("==========================================================");
        System.out.println("Inicio de simulación : " + config.initClock());
        System.out.println("Longitud de la simulación : " + config.simLength());
        System.out.println("Numero de colas : " + config.numQueues());
        System.out.println("Servidores por cola : " + config.serversPerQueue());
        System.out.println("-------------------------------------------------");
        System.out.println("Total de entidades arribadas : " + p.arrivalCounter.count());
        System.out.println("Total que iniciaron servicio : " + p.servedCounter.count());
        System.out.println("Total de entidades atendidas : " + p.departureCounter.count());
        System.out.println("-------------------------------------------------");
        
        System.out.println("Tiempo total de espera       : " + p.waitingTime.totalWaitingTime());
        System.out.println("Tiempo máximo de espera      : " + p.maxWaitingTime.maxWaitingTime());
        System.out.println("Tiempo total en el sistema   : " + p.systemTime.totalSystemTime());
        System.out.println("Tiempo máximo en el sistema  : " + p.maxSystemTime.maxSystemTime());
        System.out.println("-------------------------------------------------");
        for (int q = 0; q < config.numQueues(); q++) {
            try {
                System.out.println("Tamaño máximo de la Cola " + q + "   : " + p.maxQueueSize.maxQueueSize(q));
            } catch (IllegalArgumentException e) {
                System.out.println("Tamaño máximo de la Cola " + q + "   : 0 (sin espera)");
            }
        }
        System.out.println("-------------------------------------------------");
        int totalServers = config.numQueues() * config.serversPerQueue();
        for (int s = 0; s < totalServers; s++) {
            try {
                System.out.println("Servidor " + s + " -> Ocio Total: " + p.idleTime.totalIdleTime(s)
                    + " | Ocio Máx: " + p.maxIdleTime.maxIdleTime(s));
            } catch (IllegalArgumentException e) {
                System.out.println("Servidor " + s + " -> Sin tiempo ocioso registrado.");
            }
        }
        System.out.println("=================================================\n");
    }

    private void showAvarageResults(List<RunProbes> runs, SimulationConfig config) {
        
        // int sumaArribos = 0;
        // for (RunProbes r : runs) {
        //     sumaArribos += r.arrivalCounter.count();
        // }
        // double avgArrivals;
        // if (!runs.isEmpty()) {
        //     avgArrivals = (double) sumaArribos / runs.size();
        // } else {
        //     avgArrivals = 0.0;
        // }
        
        int totalRuns = runs.size();
        double avgArrivals = runs.stream().mapToInt(r -> r.arrivalCounter.count()).average().orElse(0.0);
        double avgServed = runs.stream().mapToInt(r -> r.servedCounter.count()).average().orElse(0.0);
        double avgDepartures = runs.stream().mapToInt(r -> r.departureCounter.count()).average().orElse(0.0);
        
        double avgWaitingTime = runs.stream().mapToDouble(r -> r.waitingTime.totalWaitingTime()).average().orElse(0.0);
        double avgMaxWaitingTime = runs.stream().mapToDouble(r -> r.maxWaitingTime.maxWaitingTime()).average().orElse(0.0);
        double avgSystemTime = runs.stream().mapToDouble(r -> r.systemTime.totalSystemTime()).average().orElse(0.0);
        double avgMaxSystemTime = runs.stream().mapToDouble(r -> r.maxSystemTime.maxSystemTime()).average().orElse(0.0);
        System.out.println("=================================================");
        System.out.printf("  PROMEDIO DE RESULTADOS (%d REPLICACIONES)%n", totalRuns);
        System.out.println("=================================================");
        System.out.println("Inicio de simulación : " + config.initClock());
        System.out.println("Longitud de la simulación : " + config.simLength());
        System.out.println("Numero de colas : " + config.numQueues());
        System.out.println("Servidores por cola : " + config.serversPerQueue());
        System.out.println("-------------------------------------------------");
        System.out.printf("Promedio de entidades arribadas : %.2f%n", avgArrivals);
        System.out.printf("Promedio que iniciaron servicio : %.2f%n", avgServed);
        System.out.printf("Promedio de entidades atendidas : %.2f%n", avgDepartures);
        System.out.println("-------------------------------------------------");
        System.out.printf("Promedio tiempo total de espera : %.2f%n", avgWaitingTime);
        System.out.printf("Promedio tiempo máximo de espera: %.2f%n", avgMaxWaitingTime);
        System.out.printf("Promedio tiempo total en sistema: %.2f%n", avgSystemTime);
        System.out.printf("Promedio tiempo máx. en sistema : %.2f%n", avgMaxSystemTime);
        System.out.println("-------------------------------------------------");
        // Métricas promedio por cola
        for (int q = 0; q < config.numQueues(); q++) {
            final int queueId = q;
            double avgMaxQueue = runs.stream()
                .mapToInt(r -> {
                    try {
                        return r.maxQueueSize.maxQueueSize(queueId);
                    } catch (IllegalArgumentException e) {
                        return 0;
                    }
                })
                .average().orElse(0.0);
            System.out.printf("Cola %d -> Tamaño máximo promedio    : %.2f%n", queueId, avgMaxQueue);
        }
        System.out.println("-------------------------------------------------");
        // Métricas promedio por servidor
        int totalServers = config.numQueues() * config.serversPerQueue();
        for (int s = 0; s < totalServers; s++) {
            final int serverId = s;
            double avgIdle = runs.stream()
                .mapToDouble(r -> {
                    try {
                        return r.idleTime.totalIdleTime(serverId);
                    } catch (IllegalArgumentException e) {
                        return 0.0;
                    }
                })
                .average().orElse(0.0);
            double avgMaxIdle = runs.stream()
                .mapToDouble(r -> {
                    try {
                        return r.maxIdleTime.maxIdleTime(serverId);
                    } catch (IllegalArgumentException e) {
                        return 0.0;
                    }
                })
                .average().orElse(0.0);
            System.out.printf("Servidor %d -> Ocio Total Promedio: %.2f | Ocio Máx Promedio: %.2f%n",
                serverId, avgIdle, avgMaxIdle);
        }
        System.out.println("=================================================\n");
    }

}
