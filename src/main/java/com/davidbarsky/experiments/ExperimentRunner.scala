package com.davidbarsky.experiments

import java.util

import com.davidbarsky.dag.models.Task
import com.davidbarsky.dag.models.states.MachineType
import com.davidbarsky.dag.{Actualizer, CostAnalyzer, TopologicalSorter}
import com.davidbarsky.schedulers.{BoundedScheduler, UnboundedScheduler}

import collection.JavaConverters._

object ExperimentRunner {
  // Make this run a generic graph
  def runSeries(scheduler: UnboundedScheduler,
                startNodes: Int,
                endNodes: Int,
                machineType: MachineType): util.List[ExperimentResult] = {
    (startNodes to endNodes).map { numNodes =>
      val graph = TopologicalSorter.generateGraph(numNodes)
      runExperiment(scheduler, numNodes, graph, machineType)
    }.asJava
  }

  // Make this run a generic graph
  def runSeries(scheduler: BoundedScheduler,
                startQueues: Int,
                endQueues: Int,
                machineType: MachineType): util.List[ExperimentResult] = {
    (startQueues to endQueues).map { numQueues =>
      val graph = TopologicalSorter.generateGraph(numQueues * 3)
      runExperiment(scheduler, numQueues, graph, machineType)
    }.asJava
  }

  def runExperiment(scheduler: UnboundedScheduler,
                    numberOfNodes: Int,
                    graph: util.List[Task],
                    machineType: MachineType): ExperimentResult = {
    val unbuiltGraph = scheduler.generateSchedule(graph, machineType)
    val builtGraph = Actualizer.actualize(unbuiltGraph)
    val cost = CostAnalyzer.findCostOfBuiltTasks(builtGraph)

    ExperimentResult(scheduler.toString,
                     machineType,
                     numberOfNodes,
                     unbuiltGraph.size,
                     cost)
  }

  def runExperiment(scheduler: BoundedScheduler,
                    numberOfQueues: Int,
                    graph: util.List[Task],
                    machineType: MachineType): ExperimentResult = {
    val unbuiltGraph = scheduler.generateSchedule(numberOfQueues, machineType)
    val builtGraph = Actualizer.actualize(unbuiltGraph)
    val cost = CostAnalyzer.findCostOfBuiltTasks(builtGraph)

    ExperimentResult(scheduler.toString,
                     machineType,
                     builtGraph.size,
                     numberOfQueues,
                     cost)
  }
}
