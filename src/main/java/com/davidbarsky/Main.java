package com.davidbarsky;

import java.util.List;

import com.davidbarsky.dag.Actualizer;
import com.davidbarsky.dag.CostAnalyzer;
import com.davidbarsky.dag.models.TaskQueue;
import com.davidbarsky.schedulers.*;


public class Main {
    public static void main(String... args) {
        // Init
        UnboundedScheduler linearCluster = new LinearCluster();
        UnboundedScheduler edgeZero = new EdgeZero();
        BoundedScheduler roundRobin = new RoundRobin();

    	List<TaskQueue> randomGraph = roundRobin.generateSchedule(2);
    	final List<TaskQueue> taskQueues = Actualizer.actualize(randomGraph);

    	System.out.println("Cost Analysis");
    	
    	if (taskQueues == null)
    		return;

        List<TaskQueue> allPaths = linearCluster.generateSchedule(40);
        Actualizer.actualize(allPaths);

        taskQueues.forEach(tqs -> {
            System.out.println(tqs.getMachineType());
            System.out.println(CostAnalyzer.findCost(tqs.getTasks(), tqs.getMachineType()));
        });

        System.out.println("Logical Schedules");
        taskQueues.forEach(System.out::println);
    }
}
