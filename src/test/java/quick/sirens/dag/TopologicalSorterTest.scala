package quick.sirens.dag

import org.junit.Assert.assertEquals
import sirens.dag.TopologicalSorter
import info.rmarcus.ggen4j.GGen
import org.junit.Test

class TopologicalSorterTest {
  @Test
  def mapToTaskList(): Unit = {
    val graph = GGen.generateGraph.erdosGNM(20, 100)
      .vertexProperty("latency").uniform(10, 30)
      .edgeProperty("networking").uniform(50, 120)
      .generateGraph.topoSort.allVertices

    val taskGraph = TopologicalSorter.mapToTaskList(graph)
    assertEquals(graph.size, taskGraph.size)
  }
}
