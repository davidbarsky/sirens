package edu.brandeis;

import java.util.List;

import edu.brandeis.dag.Actualizer;
import edu.brandeis.dag.GraphGenerator;
import edu.brandeis.dag.models.Task;
import edu.brandeis.dag.models.TaskQueue;

public class Main {
    public static void main(String... args) {
    	List<TaskQueue> tqs = GraphGenerator.randomGraph(2);

        List<TaskQueue> tasks = Actualizer.actualize(tqs);

        tasks.forEach(System.out::println);
    }
}
