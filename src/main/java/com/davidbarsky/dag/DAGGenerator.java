package com.davidbarsky.dag;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.states.MachineType;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import info.rmarcus.ggen4j.GGen;
import info.rmarcus.ggen4j.GGenCommand;
import info.rmarcus.ggen4j.GGenException;
import info.rmarcus.ggen4j.graph.GGenGraph;
import info.rmarcus.ggen4j.graph.Vertex;

public class DAGGenerator {
	
	static {
		GGenCommand.GGEN_PATH = System.getenv("GGEN_PATH");
	}
	private DAGGenerator() { }

	public static List<Collection<Vertex>> generateGraphRange(int maxNumVerticies) {
		return IntStream.range(1, maxNumVerticies + 1)
				.parallel()
				.mapToObj(DAGGenerator::getErdosGNMSources)
				.collect(Collectors.toList());
	}

	
	public static Collection<Vertex> getErdosGNMSources(int numVertices) {
		
		if (GGenCommand.GGEN_PATH == null) {
			throw new DAGException("You need to set the GGEN_PATH environmental variable!");
		}

		GGenGraph graph;
		try {
			graph = GGen.generateGraph().erdosGNM(numVertices, 100)
					.vertexProperty("latency").uniform(10, 30)
					.edgeProperty("networking").uniform(50, 120)
					.generateGraph();

			return graph.allVertices();
		} catch (GGenException e) {
			throw new DAGException(e.getMessage());
		}
	}
	
	public static Collection<Vertex> getCholesky() {
		GGenGraph graph;
		try {
			graph = GGen.staticGraph().cholesky(5)
					.vertexProperty("latency").uniform(10, 30)
					.edgeProperty("networking").uniform(10, 40)
					.generateGraph();

			System.out.println(graph.toGraphviz());
			
			return graph.allVertices();
		} catch (GGenException e) {
			throw new DAGException(e.getMessage());
		}
	}
	
	public static Collection<Task> verticesToTasks(Collection<Vertex> vertices) {
		Map<Integer, Task> tasks = new HashMap<>();
		
		int id = 0;
		for (Vertex v : vertices) {
			// TODO, for now assume latency is the same for both
			// machine types
			Map<MachineType, Integer> latency = new EnumMap<>(MachineType.class);
			double l = Double.valueOf(v.getVertexProperties().get("latency"));
			for (MachineType mt : MachineType.values()) {
				latency.put(mt, (int)l);
			}
			
			tasks.put(v.getID(), new Task(id++, latency));
		}
		
		for (Vertex v : vertices) {
			Task t = tasks.get(v.getID());
			for (Entry<Vertex, Map<String, String>> edge : v.getChildren().entrySet()) {
				Task child = tasks.get(edge.getKey().getID());
				double nw = Double.valueOf(edge.getValue().get("networking"));
				child.addDependency((int)nw, t);
			}
		}
		
		return tasks.values();
	}
}
