package com.davidbarsky.dag;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.eclipse.jdt.annotation.NonNull;

import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.TaskQueue;
import com.davidbarsky.dag.models.states.MachineType;

import info.rmarcus.NullUtils;
import info.rmarcus.ggen4j.GGen;
import info.rmarcus.ggen4j.GGenCommand;
import info.rmarcus.ggen4j.GGenException;
import info.rmarcus.ggen4j.graph.GGenGraph;
import info.rmarcus.ggen4j.graph.Vertex;

public class DAGGenerator {
	
	static {
		GGenCommand.GGEN_PATH = System.getenv("GGEN_PATH");
	}

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
					.generateGraph().topoSort();

			return graph.allVertices();
		} catch (GGenException e) {
			throw new DAGException(e.getMessage());
		}
	}
	
	public static Collection<Vertex> getCholesky(int matrixBlocks) {
		GGenGraph graph;
		try {
			graph = GGen.dataflowGraph().cholesky(matrixBlocks)
					.vertexProperty("latency").uniform(10, 60)
					.edgeProperty("networking").uniform(10, 60)
					.generateGraph().topoSort();
			
			return graph.allVertices();
		} catch (GGenException e) {
			throw new DAGException(e.getMessage());
		}
	}
	
	public static Collection<Vertex> getFibonacci(int n) {
		GGenGraph graph;
		try {
			graph = GGen.staticGraph().fibonacci(n, 1)
					.vertexProperty("latency").uniform(10, 60)
					.edgeProperty("networking").uniform(10, 60)
					.generateGraph().topoSort();
			
			return graph.allVertices();
		} catch (GGenException e) {
			throw new DAGException(e.getMessage());
		}
	}
	
	public static Collection<Vertex> getForkJoin(int n) {
		GGenGraph graph;
		try {
			graph = GGen.staticGraph().forkJoin(n, 16)
					.vertexProperty("latency").uniform(10, 60)
					.edgeProperty("networking").uniform(10, 60)
					.generateGraph().topoSort();
			
			return graph.allVertices();
		} catch (GGenException e) {
			throw new DAGException(e.getMessage());
		}
	}
	
	public static Collection<Vertex> getPoisson(int n) {
		GGenGraph graph;
		try {
			graph = GGen.dataflowGraph().poisson2D(20, n)
					.vertexProperty("latency").uniform(10, 60)
					.edgeProperty("networking").uniform(10, 60)
					.generateGraph().topoSort();
			
			return graph.allVertices();
		} catch (GGenException e) {
			throw new DAGException(e.getMessage());
		}
	}
	
	public static Collection<Vertex> getSparseLU(int n) {
		GGenGraph graph;
		try {
			graph = GGen.dataflowGraph().sparseLU(n)
					.vertexProperty("latency").uniform(10, 60)
					.edgeProperty("networking").uniform(10, 60)
					.generateGraph().topoSort();
			
			System.out.println(graph.toGraphviz());
			
			return graph.allVertices();
		} catch (GGenException e) {
			throw new DAGException(e.getMessage());
		}
	}
	

	
	public static List<Task> verticesToTasks(Collection<Vertex> vertices) {
		Map<Integer, Task> tasks = new HashMap<>();
		
		for (Vertex v : vertices) {
			// TODO, for now assume latency is the same for both
			// machine types
			Map<MachineType, Integer> latency = new EnumMap<>(MachineType.class);
			double l = Double.valueOf(v.getVertexProperties().get("latency"));
			for (MachineType mt : MachineType.values()) {
				latency.put(mt, (int)l);
			}
			
			tasks.put(v.getID(), new Task(v.getTopographicalOrder(), latency));
		}
		
		for (Vertex v : vertices) {
			Task t = tasks.get(v.getID());
			for (Entry<Vertex, Map<String, String>> edge : v.getChildren().entrySet()) {
				Task child = tasks.get(edge.getKey().getID());
				double nw = Double.valueOf(edge.getValue().get("networking"));
				child.addDependency((int)nw, t);
			}
		}
		
		return new ArrayList<>(tasks.values());
	}
	
	public static Map<String, Integer> getVertexWeightMap(Collection<Vertex> vertices) {
		return vertices.stream()
				.collect(Collectors.toMap(
						v -> "v" + v.getID(), 
						v -> Double.valueOf(v.getVertexProperties().get("latency")).intValue()));
	}
	
	public static Map<String, Map<String, Integer>> getEdgeWeightMap(Collection<Vertex> vertices) {
		Map<String, Map<String, Integer>> toR = new HashMap<>();
		
		for (Vertex v : vertices) {
			String parent = "v" + v.getID();
			Map<String, Integer> edges = new HashMap<>();
			for (Entry<Vertex, Map<String, String>> e : v.getChildren().entrySet()) {
				edges.put("v" + e.getKey().getID(), Double.valueOf(e.getValue().get("networking")).intValue());
			}
			toR.put(parent, edges);
		}
		
		return toR;
	}
	
	public static Collection<TaskQueue> clustersToTasks(Collection<Vertex> vertices, Set<List<String>> clusters) {
		Map<String, Task> m = verticesToTasks(vertices).stream()
				.collect(Collectors.toMap(v -> "v" + v.getID(), v -> v));
		
		Collection<TaskQueue> toR = new ArrayList<>(clusters.size());
		
		for (List<String> cluster : clusters) {
			TaskQueue toAdd = new TaskQueue(MachineType.SMALL);
			
			for (String vertex : cluster) {
				toAdd.add(m.get(vertex));
			}
			
			toR.add(toAdd);
		}
		
		return toR;
	}
	
	public static @NonNull List<@NonNull Task> cloneTasks(List<@NonNull Task> tasks) {
		Map<Integer, @NonNull Task> old = new HashMap<>();
		Map<Integer, @NonNull Task> toR = new HashMap<>();
		
		for (Task t : tasks) {
			Task newTask = new Task(t.getID(), t.getLatencies());
			old.put(t.getID(), t);
			toR.put(newTask.getID(), newTask);
		}
		
		for (Task t : toR.values()) {
			for (Entry<Task, Integer> depend : old.get(t.getID()).getDependencies().entrySet()) {
				t.addDependency(depend.getValue(), toR.get(depend.getKey().getID()));
			}
		}
		
		List<@NonNull Task> l = tasks.stream()
				.map(t -> toR.get(t.getID()))
				.collect(Collectors.toList());
		
		return NullUtils.orThrow(l);
		
	}
	

}
