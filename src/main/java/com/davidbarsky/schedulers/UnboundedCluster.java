package com.davidbarsky.schedulers;

import java.util.*;
import java.util.stream.IntStream;

import com.davidbarsky.dag.DAGGenerator;
import com.davidbarsky.dag.TopologicalSorter;
import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.TaskQueue;

import com.davidbarsky.dag.models.states.MachineType;
import info.rmarcus.ggen4j.graph.Vertex;

public class UnboundedCluster {
    // For each unique critical path, we'll place it on a TaskQueue.
    // The TaskQueues will be sorted in decreasing order.
    public static List<TaskQueue> linearCluster() {
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
