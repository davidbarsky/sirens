package quick.sirens.experiments

import org.junit.Assert._
import org.junit.Test
import sirens.experiments.{ExperimentRunner, GraphGenerator}
import sirens.models.data.{LatencyBounds, NetworkingBounds}
import sirens.models.states.MachineType
import sirens.schedulers.{EdgeZero, LinearCluster, RoundRobin}

class ExperimentRunnerTest {
  @Test
  @throws[Exception]
  def runSeriesUnbounded() {
    val graph = GraphGenerator.genericGraph(40)
    val result = ExperimentRunner.runExperiment(
      scheduler = new LinearCluster,
      numberOfNodes = graph.size,
      graph = graph,
      machineType = MachineType.SMALL,
      networkingBounds = NetworkingBounds(10, 60),
      latencyBounds = LatencyBounds(10, 60)
    )
    assertEquals(result.schedulerName, "LinearCluster")
  }

  @Test
  @throws[Exception]
  def runSeriesBounded() {
    val graph = GraphGenerator.genericGraph(40)
    val result = ExperimentRunner.runExperiment(
      scheduler = new RoundRobin,
      numberOfNodes = graph.size,
      numberOfQueues = 4,
      graph = graph,
      machineType = MachineType.SMALL,
      networkingBounds = NetworkingBounds(10, 60),
      latencyBounds = LatencyBounds(10, 60)
    )

    assertEquals(result.schedulerName, "RoundRobin")
  }

  @Test
  @throws[Exception]
  def runExperimentUnbounded() {
    val graph = GraphGenerator.genericGraph(50)
    val result = ExperimentRunner.runExperiment(
      scheduler = new EdgeZero,
      numberOfNodes = graph.size,
      graph = graph,
      machineType = MachineType.SMALL,
      networkingBounds = NetworkingBounds(10, 60),
      latencyBounds = LatencyBounds(10, 60)
    )
    assertEquals(result.schedulerName, "EdgeZero")
  }
}
