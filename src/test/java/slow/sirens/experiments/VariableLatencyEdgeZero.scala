package slow.sirens.experiments

import java.util

import org.junit.Test
import sirens.experiments.{ExperimentRunner, GraphGenerator}
import sirens.models.Task
import sirens.models.data.{LatencyBounds, NetworkingBounds}
import sirens.models.states.MachineType
import sirens.schedulers.EdgeZero

class VariableLatencyEdgeZero {
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
    println(s"Ratio between first and last cost: ${results.last.finalCost.toDouble /
      results.head.finalCost.toDouble}")
  }

  @Test
  def cholesky(): Unit = {
    (100 :: 200 :: 300 :: 400 :: Nil).foreach { lowerBound =>
      val latencyBounds = LatencyBounds(lowerBound, lowerBound)
      val networkingBounds = NetworkingBounds(lowerBound, lowerBound)

      val graphs = GraphGenerator.cholesky(latencyBounds, networkingBounds)
      run(graphs, latencyBounds, networkingBounds)
    }
  }

  @Test
  def erdosGNM(): Unit = {
    (100 :: 200 :: 300 :: 400 :: Nil).foreach { lowerBound =>
      val latencyBounds = LatencyBounds(lowerBound, lowerBound)
      val networkingBounds = NetworkingBounds(lowerBound, lowerBound)

      val graphs = GraphGenerator.erdos(latencyBounds, networkingBounds)
      run(graphs, latencyBounds, networkingBounds)
    }
  }

  @Test
  def erdosGNP(): Unit = {
    (100 :: 200 :: 300 :: 400 :: Nil).foreach { lowerBound =>
      val latencyBounds = LatencyBounds(lowerBound, lowerBound)
      val networkingBounds = NetworkingBounds(lowerBound, lowerBound)

      val graphs = GraphGenerator.erdosGNP(latencyBounds, networkingBounds)
      run(graphs, latencyBounds, networkingBounds)
    }
  }

  @Test
  def forkJoin(): Unit = {
    (100 :: 200 :: 300 :: 400 :: Nil).foreach { lowerBound =>
      val latencyBounds = LatencyBounds(lowerBound, lowerBound)
      val networkingBounds = NetworkingBounds(lowerBound, lowerBound)

      val graphs = GraphGenerator.forkJoin(latencyBounds, networkingBounds)
      run(graphs, latencyBounds, networkingBounds)
    }
  }

  @Test
  def poisson2D: Unit = {
    (100 :: 200 :: 300 :: 400 :: Nil).foreach { lowerBound =>
      val latencyBounds = LatencyBounds(lowerBound, lowerBound)
      val networkingBounds = NetworkingBounds(lowerBound, lowerBound)

      val graphs = GraphGenerator.poisson(latencyBounds, networkingBounds)
      run(graphs, latencyBounds, networkingBounds)
    }
  }

  @Test
  def sparseLU: Unit = {
    (100 :: 200 :: 300 :: 400 :: Nil).foreach { lowerBound =>
      val latencyBounds = LatencyBounds(lowerBound, lowerBound)
      val networkingBounds = NetworkingBounds(lowerBound, lowerBound)

      val graphs = GraphGenerator.sparseLU(latencyBounds, networkingBounds)
      run(graphs, latencyBounds, networkingBounds)
    }
  }
}
