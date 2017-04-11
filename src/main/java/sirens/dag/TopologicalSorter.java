package sirens.dag;

import java.util.*;
import java.util.stream.Collectors;

import sirens.models.Task;
import sirens.models.states.MachineType;

import info.rmarcus.ggen4j.GGen;
import info.rmarcus.ggen4j.GGenException;
import info.rmarcus.ggen4j.graph.GGenGraph;
import info.rmarcus.ggen4j.graph.Vertex;

public class TopologicalSorter {
    public static List<Task> mapToTaskList(Collection<Vertex> graph) {
        List<Task> sortedTaskGraph = new ArrayList<>(graph.size());
        Map<Integer, Task> tasks = new HashMap<>();

        // Transform them into Tasks and add them to the tasks map
        for (Vertex v : graph) {
            int vertexLatency = (int) (double) Double.valueOf(v.getVertexProperties().get("latency"));
            Map<MachineType, Integer> latencies = new EnumMap<>(MachineType.class);
            latencies.put(MachineType.SMALL, vertexLatency);

            Task t = new Task(v.getTopographicalOrder(), latencies);
            tasks.put(v.getTopographicalOrder(), t);
            sortedTaskGraph.add(t);
        }

        // Assign each task its dependencies
        for (Vertex v : graph) {
            Task t = tasks.get(v.getID());
            for (Map.Entry<Vertex, Map<String, String>> child : v.getChildren().entrySet()) {
                Integer childID = child.getKey().getTopographicalOrder();
                Task dependent = tasks.get(childID);
                Integer networkCost = (int) (double) Double.valueOf(child.getValue().get("networking"));

                dependent.addDependency(networkCost, t);
            }
        }

        return sortedTaskGraph.stream()
                .sorted(Comparator.comparingLong(Task::getID))
                .collect(Collectors.toList());
    }
}
