package quick.sirens.schedulers

import sirens.dag.{Actualizer, TopologicalSorter}
import sirens.models.Task
import org.junit.Test
import java.util

import info.rmarcus.ggen4j.GGen
import info.rmarcus.ggen4j.graph.GGenGraph
import org.junit.Assert._
import sirens.models.TaskQueue
import sirens.models.states.{BuildStatus, MachineType}
import sirens.experiments.GraphGenerator
import sirens.schedulers.{GraphProperties, LinearCluster}

import collection.JavaConverters._

class LinearClusterTest {
  private def build(graph: GGenGraph): util.List[TaskQueue] = {
    val linearCluster: LinearCluster = new LinearCluster

    val taskGraph: util.List[Task] =
      TopologicalSorter.mapToTaskList(graph.allVertices)
    val schedule: util.List[TaskQueue] =
      linearCluster.generateSchedule(taskGraph, MachineType.SMALL)

    Actualizer.actualize(schedule)
  }

  private def verifyGraph(builtGraph: util.List[TaskQueue]) = {
    assertTrue(
      builtGraph.asScala.toList
        .flatMap(_.getTasks.asScala.toList)
        .forall(_.getBuildStatus == BuildStatus.BUILT))
  }

//  @Test
//  @throws[Exception]
//  def generateScheduleWithFibonacci() {
//
//    // @formatter:off
//    val gg: GGenGraph = GGen.staticGraph.fibonacci(8, 1)
//      .vertexProperty("latency").uniform(10, 60)
//      .edgeProperty("networking").uniform(10, 60)
//      .generateGraph.topoSort
//
//    val builtGraph: util.List[TaskQueue] = build(gg)
//    verifyGraph(builtGraph)
//  }

  @Test
  def generateScheduleWithErdos() {

    // @formatter:off
    val gg: GGenGraph = GGen.generateGraph.erdosGNM(70, 100)
      .vertexProperty("latency").uniform(10, 30)
      .edgeProperty("networking").uniform(50, 120)
      .generateGraph.topoSort

    val builtGraph: util.List[TaskQueue] = build(gg)
    verifyGraph(builtGraph)
  }

  @Test
  def generateScheduleWithSparceLU() {

    // @formatter:off
    val gg: GGenGraph = GGen.dataflowGraph.sparseLU(10)
      .vertexProperty("latency").uniform(10, 30)
      .edgeProperty("networking").uniform(50, 120)
      .generateGraph.topoSort

    val builtGraph: util.List[TaskQueue] = build(gg)
    verifyGraph(builtGraph)
  }

  @Test
  def generateScheduleWithCholskey() {

    // @formatter:off
    val gg: GGenGraph = GGen.dataflowGraph.cholesky(5)
      .vertexProperty("latency").uniform(10, 30)
      .edgeProperty("networking").uniform(50, 120)
      .generateGraph.topoSort

    val builtGraph: util.List[TaskQueue] = build(gg)
    verifyGraph(builtGraph)
  }

  @Test
  def generateScheduleWithDenseLU() {

    // @formatter:off
    val gg: GGenGraph = GGen.dataflowGraph.denseLU(10)
      .vertexProperty("latency").uniform(10, 30)
      .edgeProperty("networking").uniform(50, 120)
      .generateGraph.topoSort

    val builtGraph: util.List[TaskQueue] = build(gg)
    verifyGraph(builtGraph)
  }

  @Test
  @throws[Exception]
  def generateScheduleWithForkJoin() {

    // @formatter:off
    val gg: GGenGraph = GGen.staticGraph.forkJoin(10, 15)
      .vertexProperty("latency").uniform(10, 60)
      .edgeProperty("networking").uniform(10, 60)
      .generateGraph.topoSort

    val builtGraph: util.List[TaskQueue] = build(gg)
    verifyGraph(builtGraph)
  }

  @Test
  @throws[Exception]
  def generateScheduleWithPoisson2D() {

    // @formatter:off
    val gg: GGenGraph = GGen.dataflowGraph.poisson2D(20, 6)
      .vertexProperty("latency").uniform(10, 60)
      .edgeProperty("networking").uniform(10, 60)
      .generateGraph.topoSort

    val builtGraph: util.List[TaskQueue] = build(gg)
    verifyGraph(builtGraph)
  }

  @Test
  @throws[Exception]
  def bLevelImpl() {
    val genericGraph: List[Task] =
      GraphGenerator.genericGraph(60).asScala.toList
    val linearCluster: LinearCluster = new LinearCluster
    val levels =
      GraphProperties.findBottomLevel(genericGraph, MachineType.SMALL)
    val sources = genericGraph.filter(_.isSource)

    val paths = sources.map(t => GraphProperties.findCriticalPathWithLevels(t, levels))
    paths.map(linearCluster.neighborsOfCriticalPath).foreach(println)
  }
}
