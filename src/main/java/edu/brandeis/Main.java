package edu.brandeis;

import java.util.List;

import edu.brandeis.dag.Actualizer;
import edu.brandeis.dag.CostAnalyzer;
import edu.brandeis.dag.GraphGenerator;
import edu.brandeis.dag.models.TaskQueue;

public class Main {
    public static void main(String... args) {
    	List<TaskQueue> randomGraph = GraphGenerator.randomGraph(2);

    	List<TaskQueue> taskQueues = Actualizer.actualize(randomGraph);

    	System.out.println("Cost Analysis");
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
