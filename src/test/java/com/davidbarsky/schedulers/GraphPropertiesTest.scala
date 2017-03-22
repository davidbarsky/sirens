package com.davidbarsky.schedulers

import java.util

import com.davidbarsky.dag.models.states.MachineType
import org.junit.Test
import com.davidbarsky.dag.models.Task
import com.davidbarsky.experiments.GraphGenerator
import org.junit.Assert._

import collection.JavaConverters._

class GraphPropertiesTest {
  private val graph: util.List[Task] = GraphGenerator.genericGraph(50)

  @Test
  @throws[Exception]
  def findBottomLevelForGraph() {
    val leveledGraph: Map[Task, Int] =
      GraphProperties.findBottomLevel(graph.asScala.toList, MachineType.SMALL)
    // Sanity Checks
    leveledGraph.foreach(pair => {
      assertNotNull(pair._1)
      assertNotNull(pair._2)
    })

    // B-levels must consider computation costs, so zero-values are *not* possible.
    val noZeroValuesPresent = leveledGraph.values.filter(_ == 0).sum
    assertEquals(noZeroValuesPresent, 0)
  }

  @Test
  @throws[Exception]
  def findTopLevelForGraph() {
    val leveledGraph = GraphProperties.findTopLevel(graph, MachineType.SMALL)
    // Sanity Checks
    leveledGraph.forEach((task, integer) => {
      assertNotNull(integer)
      assertNotNull(task)
    })
    val someZeroValuesPresent: Boolean =
      leveledGraph.values.stream.anyMatch(i => i == 0)
    assertTrue(someZeroValuesPresent)
  }

  @Test
  def lengthOfLongestPath() {
    val criticalPathLength: Integer = GraphProperties.lengthOfLongestCP(graph)
    assertTrue(criticalPathLength != null)
  }

  @Test
  def identifyCriticalPaths() {
    val result: util.List[util.List[Task]] =
      GraphProperties.findCriticalPath(graph)
    result.forEach(path => {
      assertFalse(path.size() == 0)
    })
  }
}
