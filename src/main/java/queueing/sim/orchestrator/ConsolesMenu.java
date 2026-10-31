package queueing.sim.orchestrator;

import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import queueing.sim.TimeGenerator;
import queueing.sim.QueueView;
import queueing.sim.strategies.StrategyBag;
import queueing.sim.strategies.FirstAvailable;

public class ConsolesMenu {
    private Scanner scanner = new Scanner(System.in);
    private DistributionAdapter adapter = new DistributionAdapter();
    private SimulationRunner runner = new SimulationRunner();
    private SimulationConfig config;

    //---------------------------------------------------------------------------------

    public void start(){
        while (true){
            System.out.println("\n---------------- MENU ----------------");
            System.out.println("1. Ejecutar Simulacion de la Entrega 1");
            System.out.println("2. Ejecutar Simulacion de la Entrega 2");
            System.out.println("3. Ejecutar Simulacion Custom");
            System.out.println("0. Salir\n");
            System.out.println("Elija una opcion: ");
            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    this.config = configEntrega1();
                    runner.run(this.config);
                    break;

                case 2:
                    this.config = configEntrega2();
                    runner.run(this.config);
                    break;

                case 3:
                    this.config = crearConfig();
                    runner.run(this.config);
                    break;

                case 0:
                    System.exit(0);
                    break;

                default:
                    System.out.print("OPCION INVALIDO\n");
                    break;
            }
        }
    }

    private SimulationConfig crearConfig(){
        System.out.print("Ingrese el tiempo de inicio de la simulacion: ");
        double initClock = scanner.nextDouble();

        System.out.print("Ingrese la duracion de la simulacion: ");
        double simLength = scanner.nextDouble();

        System.out.print("Ingrese la cantidad de pistas (colas): ");
        int numQueues = scanner.nextInt();

        System.out.print("Ingrese la cantidad de servidores por pista: ");
        int serversPerQueue = scanner.nextInt();

        TimeGenerator interArrivalGenerator = elegirDistribucion("arribos", simLength);

        TimeGenerator servicGenerator = elegirDistribucion("servicio", simLength);

        StrategyBag strategyBag = new StrategyBag(  
                                                new FirstAvailable(), 
                                                queues -> queues.stream().min(Comparator.comparingInt(QueueView::size)).orElseThrow().id(),
                                                queues -> queues.getFirst().id()
                                                );
        
        System.out.print("Cantidad de iteraciones/ejecuciones: ");
        int replications = scanner.nextInt();

        System.out.print("Modo paso a paso (\"true\" o \"false\"): ");
        Boolean stepByStep = scanner.nextBoolean();
        
        SimulationConfig config = new SimulationConfig(
                                                        initClock, 
                                                        simLength, 
                                                        numQueues, 
                                                        serversPerQueue, 
                                                        interArrivalGenerator,
                                                        servicGenerator, 
                                                        strategyBag, 
                                                        replications, 
                                                        stepByStep
                                                    );

        return config;
    }
    
    private TimeGenerator elegirDistribucion(String nombre, double simLength){
        while (true){
            System.out.println("--- Elija la distribucion para " + nombre + " ---");
            System.out.println("1. Uniforme");
            System.out.println("2. Exponencial");
            System.out.println("3. Exponencial con Hora Pico");
            System.out.println("4. Normal");
            System.out.println("5. Discreta");
            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    System.out.println("Distribucion Uniforme");
                    System.out.print("Ingrese el limite inferior (a): ");
                    double a = scanner.nextDouble();
                    System.out.print("Ingrese el limite superior (b): ");
                    double b = scanner.nextDouble();
                    return adapter.genUniform(a,b);

                case 2:
                    System.out.println("Distribucion Exponencial");
                    System.out.println("Ingrese la media (mu): ");
                    double mu_E = scanner.nextDouble();
                    return adapter.genExponential(mu_E);

                case 3:
                    System.out.println("Distribucion Exponencial con Hora Pico");
                    System.out.println("Ingrese la media normal (mu): ");
                    double mu_E_Norm = scanner.nextDouble();
                    System.out.println("Ingrese la media en hora pico: ");
                    double mu_E_HP = scanner.nextDouble();
                    return adapter.genExponentialHoraPico(mu_E_Norm, mu_E_HP, simLength);

                case 4:
                    System.out.println("Distribucion Normal");
                    System.out.println("Ingrese la media (mu): ");
                    double mu_N = scanner.nextDouble();
                    System.out.println("Ingrese la desviacion estandar (sigma): ");
                    double sigma = scanner.nextDouble();
                    return adapter.genNormal(mu_N, sigma);

                case 5:
                    System.out.println("Distribucion Discreta");
                    System.out.println("Ingrese la cantidad de valores (filas de la tabla): ");
                    int n = scanner.nextInt();

                    double values = 0.0;
                    double probabilities = 0.0;
                    Map<Double,Double> table = new HashMap<>();

                    for(int i=0; i<n; i++){
                        System.out.println("Ingrese el valor " + (i+1) + ": ");
                        values = scanner.nextDouble();
                        System.out.println("Ingrese la probabilidad del valor " + (i+1) + ": ");
                        probabilities = scanner.nextDouble();
                        table.put(values,probabilities);
                    }
                    return adapter.getDiscret(table);
                            
                default:
                    System.out.println("OPCION INVALIDO\n");
                    break;
            }
        }
    }

    private SimulationConfig configEntrega1() {
        double initClock = 0.0;
        double simLength = 40320.0;
        int numQueues = 1;
        int serversPerQueue = 1;
        TimeGenerator interArrivalGenerator = adapter.getDiscret(Map.of(10.0, 0.35, 15.0, 0.45, 17.0, 0.20));
        TimeGenerator servicGenerator = adapter.getDiscret(Map.of(8.0, 0.38, 10.0, 0.32, 13.0, 0.10, 15.0, 0.20));
        StrategyBag strategyBag = new StrategyBag(
            new FirstAvailable(),
            queues -> queues.getFirst().id(),
            queues -> queues.getFirst().id()
        );
        int replications = 1;
        boolean stepByStep = false;

        return new SimulationConfig(
            initClock,
            simLength,
            numQueues,
            serversPerQueue,
            interArrivalGenerator,
            servicGenerator,
            strategyBag,
            replications,
            stepByStep
        );
    }

    private SimulationConfig configEntrega2() {
        double initClock = 0.0;
        double simLength = 40320.0;
        int numQueues = 5;
        int serversPerQueue = 1;
        TimeGenerator interArrivalGenerator = adapter.genExponentialHoraPico(15.0, 9.0, simLength);
        TimeGenerator servicGenerator = adapter.genUniform(10.0, 25.0);
        StrategyBag strategyBag = new StrategyBag(
            new FirstAvailable(),
            queues -> queues.stream().min(Comparator.comparingInt(QueueView::size)).orElseThrow().id(),
            queues -> queues.getFirst().id()
        );
        int replications = 1;
        boolean stepByStep = false;

        return new SimulationConfig(
            initClock,
            simLength,
            numQueues,
            serversPerQueue,
            interArrivalGenerator,
            servicGenerator,
            strategyBag,
            replications,
            stepByStep
        );
    }
}
