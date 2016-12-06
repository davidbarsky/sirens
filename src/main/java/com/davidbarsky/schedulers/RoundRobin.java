package com.davidbarsky.schedulers;

import com.davidbarsky.dag.DAGGenerator;
import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.TaskQueue;
import com.davidbarsky.dag.models.states.MachineType;
import info.rmarcus.ggen4j.graph.Vertex;

import java.util.*;
import java.util.stream.IntStream;

public class RoundRobin {
    public static List<TaskQueue> invoke(int numQueues) {
        List<TaskQueue> queues = new ArrayList<>(numQueues);
        Map<Integer, Task> tasks = new HashMap<>();
        int i = 0;

        IntStream.range(0, numQueues)
                .forEach(k -> queues.add(new TaskQueue(MachineType.SMALL)));

        Collection<Vertex> vertices = DAGGenerator.getErdosGNMSources();
        // round-robin each vertex to a task queue
        for (Vertex v : vertices) {
            int vertexLatency = Integer.parseInt(v.getVertexProperties().get("latency"));

            TaskQueue tq = queues.get(i);
            i = (i + 1) % numQueues;

            Map<MachineType, Integer> latencies = new EnumMap<>(MachineType.class);
            latencies.put(MachineType.SMALL, vertexLatency);

            Task t = new Task(v.getID(), tq, latencies);
            tasks.put(v.getID(), t);
            tq.add(t);
        }

        // add dependencies
        for (Vertex v : vertices) {
            Task t = tasks.get(v.getID());
            for (Map.Entry<Vertex, Map<String, String>> child : v.getChildren().entrySet()) {
                int childID = child.getKey().getID();
                Task dependent = tasks.get(childID);
                int networkCost = Integer.parseInt(child.getValue().get("networking"));

                dependent.addDependency(networkCost, t);
            }
        }

        return queues;
    }
}
