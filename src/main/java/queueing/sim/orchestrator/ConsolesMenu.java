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
            System.out.println("------- MENU --------");
            System.out.println("1. Configurar Simulacion");
            System.out.println("2. Ejecutar Simulacion");
            System.out.println("0. Salir\n");
            System.out.println("Elija una opcion: ");
            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    this.config = crearConfig();
                    break;

                case 2:
                    if (config != null){
                        runner.run(this.config);
                    }else{
                        int continuar = 0;
                        System.out.println("CONFIGURACION NO DETECTADA");
                        System.out.println("Continuar con la configuracion defaul? ");
                        System.out.println("1. SI");
                        System.out.println("0. NO");
                        continuar = scanner.nextInt();

                        if (continuar == 1){
                            this.config = configDefault();
                            runner.run(this.config);
                        }else{
                            break;
                        }
                    }
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

        TimeGenerator interArrivalGenerator = elegirDistribucion("arribos");

        TimeGenerator servicGenerator = elegirDistribucion("servicio");

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
    
    private TimeGenerator elegirDistribucion(String nombre){
        while (true){
            System.out.println("--- Elija la distribucion para " + nombre + " ---");
            System.out.println("1. Uniforme");
            System.out.println("2. Exponencial");
            System.out.println("3. Normal");
            System.out.println("4. Discreta");
            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    System.out.println("Uniform distribution");
                    System.out.print("Ingrese el limite inferior (a): ");
                    double a = scanner.nextDouble();
                    System.out.print("Ingrese el limite superior (b): ");
                    double b = scanner.nextDouble();
                    return adapter.genUniform(a,b);

                case 2:
                    System.out.println("Exponential distribution");
                    System.out.println("Ingrese la media (mu): ");
                    double mu_E = scanner.nextDouble();
                    return adapter.genExponential(mu_E);

                case 3:
                    System.out.println("Normal distribution");
                    System.out.println("Ingrese la media (mu): ");
                    double mu_N = scanner.nextDouble();
                    System.out.println("Ingrese la desviacion estandar (sigma): ");
                    double sigma = scanner.nextDouble();
                    return adapter.genNormal(mu_N, sigma);

                case 4:
                    System.out.println("Discrete distribution");
                    System.out.println("Ingrese la cantidad de valores (filas de la tabla): ");
                    int n = scanner.nextInt();

                    double values = 0.0;
                    double probabilities = 0.0;
                    Map<Double,Double> table = new java.util.HashMap<>();

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

    private SimulationConfig configDefault() {
        // initClock -> Entrega 1: 0.0 | Entrega 2: 0.0
        double initClock = 0.0;

        // simLength -> Entrega 1: 40320.0 (4 semanas) | Entrega 2: 40320.0 (4 semanas)
        double simLength = 40320.0;

        // numQueues -> Entrega 1: 1 (única pista/cola) | Entrega 2: 5 (5 pistas con 1 cola c/u)
        int numQueues = 5;

        // serversPerQueue -> Entrega 1: 1 | Entrega 2: 1
        int serversPerQueue = 1;

        /*
         * interArrivalGenerator (Tiempos entre arribos):
         * - Entrega 1 (Tabla 1 - Empírica Discreta):
         *     Map.of(10.0, 0.35, 15.0, 0.45, 17.0, 0.20)
         *     -> adapter.getDiscret(Map.of(10.0, 0.35, 15.0, 0.45, 17.0, 0.20))
         * - Entrega 2 (Exponencial):
         *     mu = 15.0 min (fuera de hora pico) / mu = 9.0 min (hora pico 9-13hs y 20-23hs)
         *     -> adapter.genExponential(15.0)
         */
        TimeGenerator interArrivalGenerator = adapter.genExponential(15.0);

        /*
         * servicGenerator (Tiempos de aterrizaje / servicio):
         * - Entrega 1 (Tabla 2 - Empírica Discreta):
         *     Map.of(8.0, 0.38, 10.0, 0.32, 13.0, 0.10, 15.0, 0.20)
         *     -> adapter.getDiscret(Map.of(8.0, 0.38, 10.0, 0.32, 13.0, 0.10, 15.0, 0.20))
         * - Entrega 2 (Uniforme):
         *     a = 10.0 min, b = 25.0 min
         *     -> adapter.genUniform(10.0, 25.0)
         */
        TimeGenerator servicGenerator = adapter.genUniform(10.0, 25.0);

        // strategyBag -> Entrega 1: 1er servidor libre y única cola | Entrega 2: 1er servidor libre y cola más corta
        StrategyBag strategyBag = new StrategyBag(
            new FirstAvailable(),
            queues -> queues.stream().min(Comparator.comparingInt(QueueView::size)).orElseThrow().id(),
            queues -> queues.getFirst().id()
        );

        // replications -> Entrega 1: 1 | Entrega 2: 1 (En Entrega 3 son 50)
        int replications = 1;

        // stepByStep -> Entrega 1: false | Entrega 2: false
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
