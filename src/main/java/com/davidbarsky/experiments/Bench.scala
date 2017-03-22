package com.davidbarsky.experiments

import com.davidbarsky.dag.TopologicalSorter

import com.davidbarsky.dag.models.states.MachineType
import com.davidbarsky.schedulers._
import info.rmarcus.ggen4j.GGen

object Bench {
  def runExperiments(): Unit = {
    val gg = GGen
      .dataflowGraph()
      .sparseLU(14)
      .vertexProperty("latency")
      .uniform(10, 60)
      .edgeProperty("networking")
      .uniform(10, 60)
      .generateGraph()
      .topoSort()

    val tasks = TopologicalSorter.mapToTaskList(gg.allVertices())
    println(tasks)
    val results = ExperimentRunner.runExperiment(new EdgeZero(),
                                                 tasks.size(),
                                                 tasks,
                                                 MachineType.SMALL)
    println(results.toCSV)
  }
}
