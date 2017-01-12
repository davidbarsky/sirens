package com.davidbarsky;

import java.util.List;

import com.davidbarsky.dag.Actualizer;
import com.davidbarsky.dag.CostAnalyzer;
import com.davidbarsky.dag.models.TaskQueue;
import com.davidbarsky.schedulers.EdgeZero;
import com.davidbarsky.schedulers.LinearCluster;
import com.davidbarsky.schedulers.RoundRobin;
import com.davidbarsky.schedulers.Scheduler;


public class Main {
    public static void main(String... args) {
        // Init
        Scheduler linearCluster = new LinearCluster();
        Scheduler edgeZero = new EdgeZero();

    	List<TaskQueue> randomGraph = RoundRobin.generateSchedule(2);
    	final List<TaskQueue> taskQueues = Actualizer.actualize(randomGraph);

    	System.out.println("Cost Analysis");
    	
    	if (taskQueues == null)
    		return;

        List<TaskQueue> allPaths = linearCluster.generateSchedule(20);
        Actualizer.actualize(allPaths);

        edgeZero.generateSchedule(20);

        taskQueues.forEach(tqs -> {
            System.out.println(tqs.getMachineType());
            System.out.println(CostAnalyzer.findCost(tqs.getTasks(), tqs.getMachineType()));
        });

        System.out.println("Logical Schedules");
        taskQueues.forEach(System.out::println);
    }
}
