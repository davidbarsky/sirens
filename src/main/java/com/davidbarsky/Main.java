package com.davidbarsky;

import java.util.ArrayList;

import com.davidbarsky.dag.Actualizer;
import com.davidbarsky.dag.CostAnalyzer;
import com.davidbarsky.dag.models.TaskQueue;
import com.davidbarsky.schedulers.RoundRobin;
import com.davidbarsky.schedulers.UnboundedClusterPrototype;

public class Main {
    public static void main(String... args) {
    	ArrayList<TaskQueue> randomGraph = RoundRobin.invoke(2);

    	ArrayList<TaskQueue> taskQueues = Actualizer.invoke(randomGraph);

    	System.out.println("Cost Analysis");
        taskQueues.forEach(tqs -> {
            System.out.println(tqs.getMachineType());
            System.out.println(CostAnalyzer.findCost(tqs.getTasks(), tqs.getMachineType()));
        });

        System.out.println("Logical Schedules");
        taskQueues.forEach(tqs -> {
            System.out.println(tqs);
        });

        UnboundedClusterPrototype.linearCluster();
    }
}
