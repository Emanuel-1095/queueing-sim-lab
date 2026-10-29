package queueing.sim.orchestrator;

import queueing.sim.TimeGenerator;
import random.variates.*;
import java.util.Map;

public class DistributionAdapter {

    public TimeGenerator genUniform(Double a, Double b){
        // System.out.println("Uniform distribution");

        // System.out.println("Ingrese el limite inferior (a): ");
        // double a = Double.parseDouble(System.console().readLine());

        // System.out.println("Ingrese el limite superior (b): ");
        // double b = Double.parseDouble(System.console().readLine());

        return rg -> new Uniform(a, b, rg).sample();
    }

    public TimeGenerator genExponential(Double mu){
        // System.out.println("Exponential distribution");

        // System.out.println("Ingrese la media (mu): ");
        // double mu = Double.parseDouble(System.console().readLine());

        double lambda = 1.0/mu;
        return rg -> new Exponential(lambda, rg).sample();
    }

    public TimeGenerator genNormal(Double mu, Double sigma){
        // System.out.println("Normal distribution");

        // System.out.println("Ingrese la media (mu): ");
        // double mu = Double.parseDouble(System.console().readLine());

        // System.out.println("Ingrese la desviacion estandar (sigma): ");
        // double sigma = Double.parseDouble(System.console().readLine());

        return rg -> Math.max(0.0, new Normal(mu, sigma, rg).sample());
    }

    public TimeGenerator getDiscret(Map<Double, Double> table){
        // System.out.println("Discrete distribution");

        // System.out.println("Ingrese la cantidad de valores: ");
        // int n = Integer.parseInt(System.console().readLine());

        // double values = 0.0;
        // double probabilities = 0.0;
        // Map<Double,Double> table = new java.util.HashMap<>();

        // for(int i=0; i<n; i++){
        //     System.out.println("Ingrese el valor " + (i+1) + ": ");
        //     values = Double.parseDouble(System.console().readLine());

        //     System.out.println("Ingrese la probabilidad del valor " + (i+1) + ": ");
        //     probabilities = Double.parseDouble(System.console().readLine());

        //     table.put(values,probabilities);
        // }

        return rg -> new Discrete<>(table, rg).sample();
    }

}
