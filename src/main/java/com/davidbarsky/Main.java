package com.davidbarsky;

import java.util.List;

import com.davidbarsky.dag.Actualizer;
import com.davidbarsky.dag.CostAnalyzer;
import com.davidbarsky.dag.models.TaskQueue;
import com.davidbarsky.schedulers.*;

public class Main {
    public static void main(String... args) {
        // Init
        BoundedScheduler roundRobin = new RoundRobin();
        LinearCluster linearCluster = new LinearCluster();
        EdgeZero edgeZero = new EdgeZero();

        List<TaskQueue> queues = edgeZero.generateSchedule(30);
        Actualizer.actualize(queues);

        List<TaskQueue> randomGraph = roundRobin.generateSchedule(2);
    	final List<TaskQueue> taskQueues = Actualizer.actualize(randomGraph);

    	System.out.println("Cost Analysis");

    	if (taskQueues == null)
    		return;

        taskQueues.forEach(tqs -> {
            System.out.println(tqs.getMachineType());
            System.out.println(CostAnalyzer.findCostOfBuiltTasks(tqs.getTasks(), tqs.getMachineType()));
        });

        System.out.println("Logical Schedules");
        taskQueues.forEach(System.out::println);
    }
}
