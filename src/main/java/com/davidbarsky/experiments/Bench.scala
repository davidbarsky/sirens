package com.davidbarsky.experiments

import collection.JavaConverters._

import com.davidbarsky.dag.models.states.MachineType
import com.davidbarsky.schedulers._

object Bench {
  def runExperiments(): Unit = {
    val graphs = GraphGenerator.sparseLU
    val results =
      ExperimentRunner.runSeries(new EdgeZero(), graphs, MachineType.SMALL)

    results.asScala.map(_.toCSV).foreach(println)
  }
}
