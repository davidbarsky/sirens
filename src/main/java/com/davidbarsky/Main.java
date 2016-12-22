package com.davidbarsky;

import java.util.ArrayList;
import java.util.List;

import com.davidbarsky.dag.Actualizer;
import com.davidbarsky.dag.CostAnalyzer;
import com.davidbarsky.dag.TopologicalSorter;
import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.TaskQueue;
import com.davidbarsky.schedulers.RoundRobin;
import com.davidbarsky.schedulers.UnboundedCluster;

public class Main {
    public static void main(String... args) {
    	ArrayList<TaskQueue> randomGraph = RoundRobin.invoke(2);
    	final List<TaskQueue> taskQueues = Actualizer.invoke(randomGraph);

    	System.out.println("Cost Analysis");
    	
    	if (taskQueues == null)
    		return;

        UnboundedCluster unboundedCluster = new UnboundedCluster();
        unboundedCluster.linearCluster(20).forEach(System.out::println);

        taskQueues.forEach(tqs -> {
            System.out.println(tqs.getMachineType());
            System.out.println(CostAnalyzer.findCost(tqs.getTasks(), tqs.getMachineType()));
        });

        System.out.println("Logical Schedules");
        taskQueues.forEach(tqs -> {
            System.out.println(tqs);
        });
    }
}
