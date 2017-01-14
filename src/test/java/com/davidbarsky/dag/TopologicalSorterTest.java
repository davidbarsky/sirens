package com.davidbarsky.dag;

import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.states.BuildStatus;
import info.rmarcus.ggen4j.GGen;
import info.rmarcus.ggen4j.graph.GGenGraph;
import info.rmarcus.ggen4j.graph.Vertex;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TopologicalSorterTest {
  @Test
  public void generateGraph() {
    List<Task> graph = TopologicalSorter.generateGraph(20);

    assertEquals(20, graph.size());
    assertTrue(graph.stream().noneMatch(t -> t.getBuildStatus() == BuildStatus.BUILT));
  }

  @Test
  public void mapToTaskList() throws Exception {
    GGenGraph graph =
        GGen.generateGraph()
            .erdosGNM(20, 100)
            .vertexProperty("latency")
            .uniform(10, 30)
            .edgeProperty("networking")
            .uniform(50, 120)
            .generateGraph()
            .topoSort();

    List<Task> tasks = TopologicalSorter.mapToTaskList(graph);
    List<Vertex> vertices = new ArrayList<>(graph.allVertices());

    assertEquals(tasks.size(), vertices.size());
    // Testing topological order is preserved
    for (int i = 0; i < tasks.size(); i++) {
      assertEquals(tasks.get(i).getID(), vertices.get(i).getID());
    }
  }
}
