package edu.brandeis.dag;

import edu.brandeis.dag.models.Task;
import edu.brandeis.dag.models.TaskQueue;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ActualizerTest {
    @Test
    void actualize() {
        ArrayList<TaskQueue> tqs = (ArrayList<TaskQueue>) GraphGenerator.randomGraph(2);

        List<Task> tasks = Actualizer.actualize(tqs);
        List<Task> copied = new ArrayList<>(tasks);

        Collections.sort(copied);

        assertAll("Actualizer",
                () -> assertEquals(copied, tasks),
                () -> assertTrue(tasks.stream().allMatch(Task::isBuilt)),
                () -> assertTrue(tasks.stream().allMatch(Task::buildable))
        );
    }
}
