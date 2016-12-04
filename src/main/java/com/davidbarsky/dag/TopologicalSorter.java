package com.davidbarsky.dag;

import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.TaskQueue;
import com.davidbarsky.dag.models.states.BuildStatus;
import com.davidbarsky.dag.models.states.MachineType;
import com.davidbarsky.schedulers.RoundRobin;
import info.rmarcus.ggen4j.graph.Vertex;

import java.util.*;

public class TopologicalSorter {
    public static TaskQueue invoke(Collection<Vertex> graph) {
        // Using an unsorted TaskQueues for topological sort source,
        // with only one task queue.
        List<TaskQueue> unsortedTasks = RoundRobin.invoke(1);

        // Our topological sort is running the actualizer.
        ArrayList<TaskQueue> builtTasks = Actualizer
                .invoke(unsortedTasks);

        // Deconstruct the tasks, as we only care about ordering
        builtTasks
                .stream()
                .flatMap(taskQueue -> taskQueue.getTasks().stream())
                .forEach(task -> {
                    task.setBuildStatus(BuildStatus.NOT_BUILT);
                    task.setStartEndTime(Optional.empty());
                });

        return builtTasks
                .stream()
                .findFirst()
                .orElseThrow(DAGException::new);
    }
}
