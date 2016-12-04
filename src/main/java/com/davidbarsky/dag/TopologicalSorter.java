package com.davidbarsky.dag;

import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.TaskQueue;
import com.davidbarsky.dag.models.states.BuildStatus;
import com.davidbarsky.schedulers.RoundRobin;
import info.rmarcus.ggen4j.graph.Vertex;

import java.util.*;

public class TopologicalSorter {
    public static List<Task> invoke(List<Vertex> graph) {
        // Using an unsorted TaskQueues for topological sort source,
        // with only one task queue.
        List<TaskQueue> unsortedTasks = RoundRobin.invoke(1);

        // Our topological sort is running the actualizer.
        ArrayList<Task> builtTasks = Actualizer
                .invoke(unsortedTasks)
                .stream()
                .findFirst()
                .orElseThrow(DAGException::new)
                .getTasks();

        // Deconstruct the tasks, as we only care about ordering
        builtTasks
                .forEach(task -> {
                    task.setBuildStatus(BuildStatus.NOT_BUILT);
                    task.setStartEndTime(Optional.empty());
                });

        return builtTasks;
    }
}
