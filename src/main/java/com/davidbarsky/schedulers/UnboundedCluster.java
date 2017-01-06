package com.davidbarsky.schedulers;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

import com.davidbarsky.dag.TopologicalSorter;
import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.TaskQueue;

import com.davidbarsky.dag.models.states.MachineType;

public class UnboundedCluster {
    private UnboundedCluster() {}

    public static List<TaskQueue> linearCluster(int numNodes) {
        List<Task> tasks = TopologicalSorter.apply(numNodes);
        List<TaskQueue> paths = new ArrayList<>();
        Set<Task> visited = new HashSet<>();

        tasks.forEach(t -> linearClusterDFS(
                t,
                new TaskQueue(MachineType.SMALL),
                paths,
                visited
        ));

        return paths.stream().distinct().collect(Collectors.toList());
    }

    private static void linearClusterDFS(Task task, TaskQueue path, List<TaskQueue> paths, Set<Task> visited) {
        visited.add(task);
        path.add(task);
        paths.add(path);

        task.getDependents().keySet()
                .stream()
                .filter(t -> !visited.contains(t))
                .forEach(t -> {
                    linearClusterDFS(t, path, paths, visited);
                });
    }

//    private void linearClusterDFS2(Task task,
//                                  List<Task> path) {
//        if (task.isSource()) {
//            List<Task> newPath = new ArrayList<>();
//            for (Task child : task.getDependents().keySet()) {
//                newPath.add(task);
//                linearClusterDFS2(child, newPath);
//            }
//        }
//
//        if (task.isLeaf()) {
//            path.add(task);
//            taskQueues.add(new TaskQueue(MachineType.SMALL, path));
//            return;
//        }
//
//        for (Task child : task.getDependents().keySet()) {
//            path.add(child);
//            linearClusterDFS2(child, path);
//        }
//    }
}
