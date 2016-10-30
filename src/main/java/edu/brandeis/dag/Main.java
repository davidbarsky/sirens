package edu.brandeis.dag;
import java.util.List;

import edu.brandeis.dag.models.TaskQueue;

public class Main {
    public static void main(String... args) {
    	List<TaskQueue> tqs = GraphGenerator.randomGraph(2);
    	Actualizer.actualize(tqs);
    	
    	tqs.forEach(System.out::println);
    }
}
