package com.davidbarsky.dag;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.davidbarsky.dag.models.TaskQueue;
import com.davidbarsky.dag.models.states.BuildStatus;
import com.davidbarsky.schedulers.RoundRobin;

import info.rmarcus.ggen4j.graph.Vertex;

public class TopologicalSorter {
    public static List<TaskQueue> invoke(Collection<Vertex> graph) {
        // Using an unsorted TaskQueues for topological sort source,
        // with only one task queue.
        List<TaskQueue> unsortedTasks = RoundRobin.invoke(graph.size());

        // Our topological sort is running the actualizer.
        final List<TaskQueue> builtTasks = Actualizer
                .invoke(unsortedTasks);

        if (builtTasks == null)
        	return null;
        
        // Deconstruct the tasks, as we only care about ordering
        builtTasks
                .stream()
                .flatMap(taskQueue -> taskQueue.getTasks().stream())
                .forEach(task -> {
                    task.setBuildStatus(BuildStatus.NOT_BUILT);
                    task.setStartEndTime(Optional.empty());
                });

        return builtTasks;
    }
}
