package edu.brandeis.dag;

import edu.brandeis.dag.models.Task;
import edu.brandeis.dag.models.TaskQueue;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CosterTest {
    @Test
    void findCost() {
        List<TaskQueue> tqs = GraphGenerator.randomGraph(2);
        List<TaskQueue> tasks = Actualizer.actualize(tqs);

        System.out.println(Coster.findCost(tasks));
    }
}
