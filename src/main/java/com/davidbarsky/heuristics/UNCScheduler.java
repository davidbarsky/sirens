package com.davidbarsky.heuristics;

import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.TaskQueue;

import java.util.*;
import java.util.stream.Collectors;

public class UNCScheduler {
    public List<List<Task>> linearCluster(List<TaskQueue> taskQueues) {
        Set<Task> visited = new HashSet<>();
        List<List<Task>> criticalPaths = new ArrayList<>();

        for (TaskQueue taskQueue : taskQueues) {
            Queue<Task> queue = new LinkedList<>();
            Task head = taskQueue.getTasks().get(0);
            List<Task> path = new ArrayList<>();

            queue.add(head);
            while (!queue.isEmpty()) {
                Task current = queue.remove();
                for (Task neighbor : current.getDependents().keySet()) {
                    if (!visited.contains(neighbor)) {
                        queue.add(neighbor);
                        path.add(neighbor);
                        visited.add(neighbor);
                    }
                }
            }

            criticalPaths.add(path);
        }

        return criticalPaths;
    }

    public List<Task> edgeZero(List<TaskQueue> taskQueues) {
        ArrayList<Task> tasks = flattenTaskQueues(taskQueues);
        LinkedHashSet<Task> visited = new LinkedHashSet<>();

        while (tasks.size() != visited.size()) {
            Task highestNetworkCost = tasks.get(0);
            visited.add(highestNetworkCost);
            highestNetworkCost.getDependents().replace(highestNetworkCost, 0);
        }

        return visited.stream().collect(Collectors.toList());
    }

    private ArrayList<Task> flattenTaskQueues(List<TaskQueue> taskQueues) {
        ArrayList<Task> tasks = new ArrayList<>();
        taskQueues.forEach(tq -> {
            tasks.addAll(tq.getTasks());
        });
        Collections.sort(tasks, Comparator.comparingInt(t -> t.getDependents().get(t)));
        return tasks;
    }
}
