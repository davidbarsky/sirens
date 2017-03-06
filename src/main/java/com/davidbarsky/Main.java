package com.davidbarsky;


import com.davidbarsky.dag.models.states.MachineType;
import com.davidbarsky.experiments.ExperimentResult;
import com.davidbarsky.experiments.ExperimentRunner;
import com.davidbarsky.schedulers.*;

public class Main {
    public static void main(String... args) {
        BoundedScheduler roundRobin = new RoundRobin();
        EdgeZero edgeZero = new EdgeZero();

        ExperimentResult experimentResult = ExperimentRunner.runExperiment(edgeZero, 1540, MachineType.SMALL);
        System.out.println(experimentResult);
    }
}
