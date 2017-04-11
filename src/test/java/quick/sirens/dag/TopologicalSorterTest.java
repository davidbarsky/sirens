package quick.sirens.dag;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.List;

import sirens.dag.TopologicalSorter;
import sirens.models.Task;
import info.rmarcus.ggen4j.GGen;
import info.rmarcus.ggen4j.graph.Vertex;
import org.junit.Test;

public class TopologicalSorterTest {
    @Test
    public void mapToTaskList() throws Exception {
        Collection<Vertex> graph = GGen.generateGraph().erdosGNM(20, 100)
                .vertexProperty("latency").uniform(10, 30)
                .edgeProperty("networking").uniform(50, 120)
                .generateGraph().topoSort().allVertices();

        List<Task> taskGraph = (List<Task>) TopologicalSorter.mapToTaskList(graph);
        assertEquals(graph.size(), taskGraph.size());
    }
}
