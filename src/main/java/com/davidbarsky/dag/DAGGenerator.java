package com.davidbarsky.dag;
import java.util.Collection;

import info.rmarcus.ggen4j.GGen;
import info.rmarcus.ggen4j.GGenCommand;
import info.rmarcus.ggen4j.GGenException;
import info.rmarcus.ggen4j.graph.GGenGraph;
import info.rmarcus.ggen4j.graph.Vertex;

public class DAGGenerator {
	private DAGGenerator() { }
	public static Collection<Vertex> getErdosGNMSources() {
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
