package com.davidbarsky.experiments

import com.davidbarsky.dag.models.states.MachineType
import com.davidbarsky.schedulers._

object Bench {
  def runExperiments(): Unit = {
    val graphs = GraphGenerator.cholesky
    val results = ExperimentRunner.runSeries(new LinearCluster(), graphs, MachineType.SMALL)
    results.foreach(println)
  }
}
