package com.davidbarsky.dag;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.TaskQueue;
import com.davidbarsky.dag.models.states.MachineType;
import info.rmarcus.ggen4j.GGen;
import info.rmarcus.ggen4j.GGenCommand;
import info.rmarcus.ggen4j.GGenException;
import info.rmarcus.ggen4j.graph.GGenGraph;
import info.rmarcus.ggen4j.graph.Vertex;

public class GraphGenerator {
	private GraphGenerator() { }
	
	public static List<TaskQueue> randomGraph(int numQueues) {
		List<TaskQueue> queues = new ArrayList<>(numQueues);
		Map<Integer, Task> tasks = new HashMap<>();
		int i = 0;

		IntStream.range(0, numQueues)
		.forEach(k -> queues.add(new TaskQueue(MachineType.SMALL)));

		Collection<Vertex> vertices = getErdosGNMSources();
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

	protected static Collection<Vertex> getErdosGNMSources() {
		GGenCommand.GGEN_PATH = System.getenv("GGEN_PATH");
		if (GGenCommand.GGEN_PATH == null) {
			throw new DAGException("You need to set the GGEN_PATH environmental variable!");
		}

		GGenGraph graph;
		try {
			graph = GGen.generateGraph().erdosGNM(20, 100)
					.vertexProperty("latency").uniform(10, 30)
					.edgeProperty("networking").uniform(5, 20)
					.generateGraph();

			return graph.allVertices();
		} catch (GGenException e) {
			throw new DAGException(e.getMessage());
		}
	}
}
