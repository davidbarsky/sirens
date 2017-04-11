package quick.sirens.experiments

import java.util.{List => JavaList}

import org.junit.Assert._
import org.junit.Test
import sirens.experiments.{ExperimentResult, ExperimentRunner, GraphGenerator}
import sirens.models.Task
import sirens.models.states.MachineType
import sirens.schedulers.{EdgeZero, RoundRobin}

class ExperimentRunnerTest {
  @Test
  @throws[Exception]
  def runSeriesUnbounded() {
    val graph = GraphGenerator.genericGraph(40)
    val result: ExperimentResult =
      ExperimentRunner.runExperiment(new EdgeZero, graph.size, graph, MachineType.SMALL)
  }

  @Test
  @throws[Exception]
  def runSeriesBounded() {
    val graph = GraphGenerator.genericGraph(40)
    val result: ExperimentResult =
      ExperimentRunner.runExperiment(new RoundRobin, graph.size, graph, MachineType.SMALL)

    assertEquals(result.schedulerName, "RoundRobin")
  }

  @Test
  @throws[Exception]
  def runExperimentUnbounded() {
    val genericGraph: JavaList[Task] = GraphGenerator.genericGraph(50)
    val experimentResult: ExperimentResult = ExperimentRunner.runExperiment(new EdgeZero,
                                                                            genericGraph.size,
                                                                            genericGraph,
                                                                            MachineType.SMALL)
    assert(experimentResult.numberOfNodes > 0)
    assertEquals(experimentResult.schedulerName, "EdgeZero")
    assertNotNull(experimentResult.finalCost)
  }

  @Test
  @throws[Exception]
  def runExperimentBounded() {
    val genericGraph: JavaList[Task] = GraphGenerator.genericGraph(50)
    val experimentResult: ExperimentResult =
      ExperimentRunner.runExperiment(new RoundRobin, 5, genericGraph, MachineType.SMALL)

    assertEquals(experimentResult.numberOfQueues, 5)
    assertEquals(experimentResult.schedulerName, "RoundRobin")
    assertNotNull(experimentResult.finalCost)
  }
}
