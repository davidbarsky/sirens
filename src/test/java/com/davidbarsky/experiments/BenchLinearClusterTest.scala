package com.davidbarsky.experiments

import java.util

import com.davidbarsky.dag.models.states.MachineType
import com.davidbarsky.dag.models.Task
import com.davidbarsky.schedulers.LinearCluster
import org.junit.Test

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
  def erdos(): Unit = {
    val graphs = GraphGenerator.erdos
    run(graphs)
  }

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