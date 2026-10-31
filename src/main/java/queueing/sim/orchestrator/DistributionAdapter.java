package queueing.sim.orchestrator;

import queueing.sim.TimeGenerator;
import random.variates.*;
import java.util.Map;

public class DistributionAdapter {

    public TimeGenerator genUniform(Double a, Double b){
        return rg -> new Uniform(a, b, rg).sample();
    }

    public TimeGenerator genExponential(Double mu){
        double lambda = 1.0/mu;
        return rg -> new Exponential(lambda, rg).sample();
    }

    public TimeGenerator genNormal(Double mu, Double sigma){
        return rg -> Math.max(0.0, new Normal(mu, sigma, rg).sample());
    }

    public TimeGenerator getDiscret(Map<Double, Double> table){
        return rg -> new Discrete<>(table, rg).sample();
    }

    public TimeGenerator genExponentialHoraPico(Double muNormal, Double muPico, Double simLength) {
        double[] clockAcumulado = {0.0};

        return rg -> {
            // Si empieza una nueva replicación, reiniciamos el reloj acumulado
            if (clockAcumulado[0] > simLength) {
                clockAcumulado[0] = 0.0;
            }

            double horaDelDia = (clockAcumulado[0] % 1440.0) / 60.0;
            boolean esHoraPico = (horaDelDia >= 9.0 && horaDelDia < 13.0) || (horaDelDia >= 20.0 && horaDelDia < 23.0);

            double muActual;
            if (esHoraPico){
                muActual = muPico;
            }else{
                muActual = muNormal;
            }
            
            double lambda = 1.0 / muActual;
            double dt = new Exponential(lambda, rg).sample();
            clockAcumulado[0] += dt; // Avanzamos el reloj para el próximo arribo
            return dt;
        };
    }
}
