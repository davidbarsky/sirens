package slow.sirens.experiments

import java.util

import org.junit.Test
import sirens.experiments.{ExperimentRunner, GraphGenerator}
import sirens.models.Task
import sirens.models.data.{LatencyBounds, NetworkingBounds}
import sirens.models.states.MachineType
import sirens.schedulers.EdgeZero

class VariableLatencyEdgeZeroBench {
  def run(graphs: util.List[util.List[Task]],
          latencyBounds: LatencyBounds,
          networkingBounds: NetworkingBounds): Unit = {
    val results = ExperimentRunner.runSeries(
      scheduler = new EdgeZero,
      graphs = graphs,
      machineType = MachineType.SMALL,
      networkingBounds = networkingBounds,
      latencyBounds = latencyBounds
    )
    results.map(_.toCSV).foreach(println)
  }

  @Test
  def cholesky(): Unit = {
    (100 :: 200 :: 300 :: 400 :: Nil).foreach { lowerBound =>
      val latencyBounds = LatencyBounds(lowerBound, lowerBound + 50)
      val networkingBounds = NetworkingBounds(lowerBound, lowerBound + 50)

      val graphs = GraphGenerator.cholesky(LatencyBounds(lowerBound, lowerBound + 50),
                                           NetworkingBounds(lowerBound, lowerBound + 50))
      run(graphs, latencyBounds, networkingBounds)
    }
  }
}
