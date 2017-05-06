package quick.sirens.schedulers

import sirens.dag.Actualizer
import sirens.dag.CostAnalyzer
import sirens.dag.TopologicalSorter
import sirens.models.Task
import sirens.models.TaskQueue
import sirens.models.states.MachineType
import info.rmarcus.ggen4j.GGen
import info.rmarcus.ggen4j.graph.GGenGraph
import org.junit.Test
import sirens.schedulers.EdgeZero
import java.util
import org.junit.Assert._

class EdgeZeroTest {
  @throws[Exception]
  private def verifyGraph(builtGraph: util.List[TaskQueue]) = {
    CostAnalyzer.findCostOfBuiltTasks(builtGraph)
    builtGraph.forEach((tq: TaskQueue) => {
      def foo(tq: TaskQueue) = {
        assertNotNull(tq.getStartTime)
        assertNotNull(tq.getEndTime)
        assertTrue(0 != tq.geEndTimeOfLastBuiltTask)
        tq.getTasks.forEach((t: Task) => {
          def foo(t: Task) =
            assertTrue(t.isBuilt)
          foo(t)
        })
      }

      foo(tq)
    })
  }

  private def build(graph: GGenGraph) = {
    val edgeZero = new EdgeZero
    val taskGraph = TopologicalSorter.mapToTaskList(graph.allVertices)
    val schedule = edgeZero.generateSchedule(taskGraph, MachineType.SMALL)

    Actualizer.actualize(schedule)
  }

  @Test
  def generateScheduleWithErdos(): Unit = {
    val gg = GGen.generateGraph
      .erdosGNM(70, 100)
      .vertexProperty("latency")
      .uniform(10, 30)
      .edgeProperty("networking")
      .uniform(50, 120)
      .generateGraph
      .topoSort
    val builtGraph = build(gg)
    verifyGraph(builtGraph)
  }

  @Test
  def generateScheduleWithSparceLU(): Unit = {
    val gg = GGen.dataflowGraph
      .sparseLU(10)
      .vertexProperty("latency")
      .uniform(10, 30)
      .edgeProperty("networking")
      .uniform(50, 120)
      .generateGraph
      .topoSort
    val builtGraph = build(gg)
    verifyGraph(builtGraph)
  }

  @Test
  def generateScheduleWithCholskey(): Unit = {
    val gg = GGen.dataflowGraph
      .cholesky(16)
      .vertexProperty("latency")
      .uniform(10, 30)
      .edgeProperty("networking")
      .uniform(50, 120)
      .generateGraph
      .topoSort
    val builtGraph = build(gg)
    verifyGraph(builtGraph)
  }

  @Test
  def generateScheduleWithDenseLU(): Unit = {
    val gg = GGen.dataflowGraph
      .denseLU(10)
      .vertexProperty("latency")
      .uniform(10, 30)
      .edgeProperty("networking")
      .uniform(50, 120)
      .generateGraph
      .topoSort
    val builtGraph = build(gg)
    verifyGraph(builtGraph)
  }

  @Test
  def generateScheduleWithForkJoin(): Unit = {
    val gg = GGen.staticGraph
      .forkJoin(10, 15)
      .vertexProperty("latency")
      .uniform(10, 60)
      .edgeProperty("networking")
      .uniform(10, 60)
      .generateGraph
      .topoSort
    val builtGraph = build(gg)
    verifyGraph(builtGraph)
  }

  @Test
  def generateScheduleWithPoisson2D(): Unit = {
    val gg = GGen.dataflowGraph
      .poisson2D(20, 6)
      .vertexProperty("latency")
      .uniform(10, 60)
      .edgeProperty("networking")
      .uniform(10, 60)
      .generateGraph
      .topoSort
    val builtGraph = build(gg)
    verifyGraph(builtGraph)
  }
}
