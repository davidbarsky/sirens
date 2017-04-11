package slow.sirens.experiments

import java.util

import org.junit.Test
import sirens.experiments.{ExperimentRunner, GraphGenerator}
import sirens.models.Task
import sirens.models.states.MachineType
import sirens.schedulers.LinearCluster

class BenchLinearClusterTest {
  def run(graphs: util.List[util.List[Task]]): Unit = {
    val results = ExperimentRunner.runSeries(new LinearCluster, graphs, MachineType.SMALL)
    results.map(_.toCSV).foreach(println)
  }

  @Test
  def cholskey(): Unit = {
    val graphs = GraphGenerator.cholesky
    run(graphs)
  }

  @Test
  def erdosGNM(): Unit = {
    val graphs = GraphGenerator.erdos
    run(graphs)
  }

//  @Test
//  def erdosGNP(): Unit = {
//    val graphs = GraphGenerator.erdosGNP
//    run(graphs)
//  }
//
//  @Test
//  def fibonacci(): Unit = {
//    val graphs = GraphGenerator.fibonacci
//    run(graphs)
//  }

  @Test
  def forkJoin(): Unit = {
    val graphs = GraphGenerator.forkJoin
    run(graphs)
  }

  @Test
  def poisson2D(): Unit = {
    val graphs = GraphGenerator.poisson
    run(graphs)
  }
  @Test
  def sparseLU(): Unit = {
    val graphs = GraphGenerator.sparseLU
    run(graphs)
  }
}
