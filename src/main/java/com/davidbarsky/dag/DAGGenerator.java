package com.davidbarsky.dag;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import info.rmarcus.ggen4j.GGen;
import info.rmarcus.ggen4j.GGenCommand;
import info.rmarcus.ggen4j.GGenException;
import info.rmarcus.ggen4j.graph.GGenGraph;
import info.rmarcus.ggen4j.graph.Vertex;

public class DAGGenerator {
	public DAGGenerator() { }

	public List<Collection<Vertex>> generateGraphRange(int maxNumVerticies) {
		return IntStream.range(1, maxNumVerticies + 1)
				.parallel()
				.mapToObj(this::getErdosGNMSources)
				.collect(Collectors.toList());
	}

	public Collection<Vertex> getErdosGNMSources(int numVertecies) {
		GGenCommand.GGEN_PATH = System.getenv("GGEN_PATH");
		if (GGenCommand.GGEN_PATH == null) {
			throw new DAGException("You need to set the GGEN_PATH environmental variable!");
		}

		GGenGraph graph;
		try {
			graph = GGen.generateGraph().erdosGNM(numVertecies, 100)
					.vertexProperty("latency").uniform(10, 30)
					.edgeProperty("networking").uniform(5, 20)
					.generateGraph();

			return graph.allVertices();
		} catch (GGenException e) {
			throw new DAGException(e.getMessage());
		}
	}
}
