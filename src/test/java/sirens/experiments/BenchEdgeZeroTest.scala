package sirens.experiments

import java.util

import sirens.dag.models.states.MachineType
import sirens.dag.models.Task
import sirens.schedulers.EdgeZero

import org.junit.Test

class BenchEdgeZeroTest {
  def runEdgeZero(graphs: util.List[util.List[Task]]): Unit = {
    val results = ExperimentRunner.runSeries(new EdgeZero, graphs, MachineType.SMALL)
    results.map(_.toCSV).foreach(println)
  }

  @Test
  def cholskey(): Unit = {
    val graphs = GraphGenerator.cholesky
    runEdgeZero(graphs)
  }

  @Test
  def erdosGNM(): Unit = {
    val graphs = GraphGenerator.erdos
    runEdgeZero(graphs)
  }

  @Test
  def erdosGNP(): Unit = {
    val graphs = GraphGenerator.erdosGNP
    runEdgeZero(graphs)
  }

  @Test
  def fibonacci(): Unit = {
    val graphs = GraphGenerator.fibonacci
    runEdgeZero(graphs)
  }

  @Test
  def forkJoin(): Unit = {
    val graphs = GraphGenerator.forkJoin
    runEdgeZero(graphs)
  }

  @Test
  def poisson2D(): Unit = {
    val graphs = GraphGenerator.poisson
    runEdgeZero(graphs)
  }

  @Test
  def sparseLU(): Unit = {
    val graphs = GraphGenerator.sparseLU
    runEdgeZero(graphs)
  }
}
