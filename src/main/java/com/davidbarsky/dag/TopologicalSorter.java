package com.davidbarsky.dag;

import java.util.*;

import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.TaskQueue;
import com.davidbarsky.dag.models.states.BuildStatus;
import com.davidbarsky.dag.models.states.MachineType;
import com.davidbarsky.schedulers.BoundedScheduler;
import com.davidbarsky.schedulers.RoundRobin;

import info.rmarcus.ggen4j.GGen;
import info.rmarcus.ggen4j.GGenException;
import info.rmarcus.ggen4j.graph.GGenGraph;
import info.rmarcus.ggen4j.graph.Vertex;

public class TopologicalSorter {
    public static List<Task> generateGraph(int numVerticies) {
        try {
            GGenGraph graph = GGen.generateGraph().erdosGNM(numVerticies, 100)
                    .vertexProperty("latency").uniform(10, 30)
                    .edgeProperty("networking").uniform(50, 120)
                    .generateGraph().topoSort();

            return mapToTaskList(graph);
        } catch (GGenException e) {
            throw new DAGException(e.getMessage());
        }
    }

    public static List<Task> mapToTaskList(GGenGraph graph) {
        List<Task> sortedTaskGraph = new ArrayList<>();
        Map<Integer, Task> tasks = new HashMap<>();

        // Transform them into Tasks and add them to the tasks map
        for (Vertex v : graph.allVertices()) {
            int vertexLatency = (int) (double) Double.valueOf(v.getVertexProperties().get("latency"));
            Map<MachineType, Integer> latencies = new EnumMap<>(MachineType.class);
            latencies.put(MachineType.SMALL, vertexLatency);

            Task t = new Task(v.getID(), latencies);
            tasks.put(v.getID(), t);
            sortedTaskGraph.add(v.getTopographicalOrder(), t);
        }

        // Assign each task its dependencies
        for (Vertex v : graph.allVertices()) {
            Task t = tasks.get(v.getID());
            for (Map.Entry<Vertex, Map<String, String>> child : v.getChildren().entrySet()) {
                Integer childID = child.getKey().getID();
                Task dependent = tasks.get(childID);
                Integer networkCost = (int) (double) Double.valueOf(child.getValue().get("networking"));

                dependent.addDependency(networkCost, t);
            }
        }

        return sortedTaskGraph;
    }
}
