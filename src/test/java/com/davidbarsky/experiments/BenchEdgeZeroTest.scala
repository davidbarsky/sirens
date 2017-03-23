package com.davidbarsky.experiments

import java.util

import com.davidbarsky.dag.models.states.MachineType
import com.davidbarsky.dag.models.Task
import com.davidbarsky.schedulers.{EdgeZero, LinearCluster}
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
  def erdos(): Unit = {
    val graphs = GraphGenerator.erdos
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
