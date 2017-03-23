package com.davidbarsky.experiments

import com.davidbarsky.dag.models.Task
import com.davidbarsky.dag.models.states.MachineType
import com.davidbarsky.schedulers.EdgeZero
import com.davidbarsky.schedulers.RoundRobin
import org.junit.Assert
import org.junit.Test
import java.util.List
import org.junit.Assert._

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
    val genericGraph: List[Task] = GraphGenerator.genericGraph(50)
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
    val genericGraph: List[Task] = GraphGenerator.genericGraph(50)
    val experimentResult: ExperimentResult =
      ExperimentRunner.runExperiment(new RoundRobin, 5, genericGraph, MachineType.SMALL)

    assertEquals(experimentResult.numberOfQueues, 5)
    assertEquals(experimentResult.schedulerName, "RoundRobin")
    assertNotNull(experimentResult.finalCost)
  }
}
