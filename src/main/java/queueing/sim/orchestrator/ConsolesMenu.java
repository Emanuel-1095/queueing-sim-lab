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
    private DistributionAdapter distributionAdapter = new DistributionAdapter();
    private SimulationRunner runner = new SimulationRunner();
    private SimulationConfig config;

    //---------------------------------------------------------------------------------

    public void start(){
        while (true){
            System.out.print("------- MENU --------");
            System.out.print("1. Configurar Simulacion");
            System.out.print("2. Ejecutar Simulacion");
            System.out.print("0. Salir\n");
            System.out.print("Elija una opcion: ");
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
                        System.out.print("CONFIGURACION NO DETECTADA");
                        System.out.print("Continuar con la configuracion defaul? ");
                        System.out.print("1. SI");
                        System.out.print("0. NO");
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
                    System.out.print("VALOR INVALIDO\n");
                    break;
            }
        }
    }

    private SimulationConfig crearConfig(){
        
    }
    
    private TimeGenerator elegirDistribucion(String nombre){

    }

    private SimulationConfig configDefault(){

    }
}
