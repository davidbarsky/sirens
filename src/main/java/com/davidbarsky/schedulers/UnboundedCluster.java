package com.davidbarsky.schedulers;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.davidbarsky.dag.DAGGenerator;
import com.davidbarsky.dag.TopologicalSorter;
import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.TaskQueue;

import com.davidbarsky.dag.models.states.MachineType;
import info.rmarcus.ggen4j.graph.Vertex;

public class UnboundedCluster {
    private List<TaskQueue> taskQueues;

    public UnboundedCluster() {
        taskQueues = new ArrayList<>();
    }

    public List<TaskQueue> linearCluster(int numNodes) {
        List<Task> tasks = TopologicalSorter.apply(numNodes);
        tasks.stream().filter(Task::isSource).forEach(t -> linearClusterDFS(t, new ArrayList<>()));
        return taskQueues.stream().distinct().collect(Collectors.toList());
    }

    private void linearClusterDFS(Task task,
                                  List<Task> path) {
        if (task.isSource()) {
            List<Task> newPath = new ArrayList<>();
            for (Task child : task.getDependents().keySet()) {
                newPath.add(task);
                linearClusterDFS(child, newPath);
            }
        }

        if (task.isLeaf()) {
            taskQueues.add(new TaskQueue(MachineType.SMALL, path.stream().distinct().collect(Collectors.toList())));
            return;
        }

        for (Task child : task.getDependents().keySet()) {
            path.add(child);
            linearClusterDFS(child, path);
        }
    }

    // For each unique critical path, we'll place it on a TaskQueue.
    // The TaskQueues will be sorted in decreasing order.
    public static List<TaskQueue> linearCluster2() {
        List<TaskQueue> queues = new ArrayList<>();
        Map<Integer, Task> tasks = new HashMap<>();
        Collection<Vertex> vertices = DAGGenerator.getErdosGNMSources(20);
        int i = 0;

        IntStream.range(0, vertices.size())
                .forEach(k -> queues.add(new TaskQueue(MachineType.SMALL)));

        for (Vertex vertex : vertices) {
            int vertexLatency = (int) (double) Double.valueOf(vertex.getVertexProperties().get("latency"));
            TaskQueue tq = queues.get(i);

            Map<MachineType, Integer> latencies = new EnumMap<>(MachineType.class);
            latencies.put(MachineType.SMALL, vertexLatency);

            Task t = new Task(vertex.getID(), tq, latencies);

            // add first node
            tasks.put(vertex.getID(), t);
            tq.add(t);

            // Add children of a given task, long line.
            List<Task> children = childrenOfVertexToPath(vertex, tasks);
            children.forEach(c -> tq.add(c));

            i += 1;
        }
        return queues;
    }

    private static List<Task> childrenOfVertexToPath(Vertex vertex, Map<Integer, Task> tasks) {
        List<Task> path = new ArrayList<>();

        Task t = tasks.get(vertex.getID());
        for (Map.Entry<Vertex, Map<String, String>> child : vertex.getChildren().entrySet()) {
            Integer childID = child.getKey().getID();
            Task dependent = tasks.get(childID);
            int networkCost = (int) (double) Double.valueOf(child.getValue().get("networking"));

            dependent.addDependency(networkCost, t);
            path.add(dependent);
        }

        return path;
    }
}
