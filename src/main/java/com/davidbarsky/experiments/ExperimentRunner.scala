package com.davidbarsky.experiments

import java.util

import com.davidbarsky.dag.models.states.MachineType
import com.davidbarsky.dag.{Actualizer, CostAnalyzer}
import com.davidbarsky.schedulers.{BoundedScheduler, UnboundedScheduler}

import scala.collection.JavaConverters._

object ExperimentRunner {
  def runSeries(scheduler: UnboundedScheduler,
                startNodes: Int,
                endNodes: Int,
                machineType: MachineType): util.List[ExperimentResult] = {
    (startNodes to endNodes).map { numNodes =>
      runExperiment(scheduler, numNodes, machineType)
    }.asJava
  }

  def runSeries(scheduler: BoundedScheduler,
                startQueues: Int,
                endQueues: Int,
                machineType: MachineType): util.List[ExperimentResult] = {
    (startQueues to endQueues).map { numQueues =>
      runExperiment(scheduler, numQueues, machineType)
    }.asJava
  }

  def runExperiment(scheduler: UnboundedScheduler,
                    numberOfNodes: Int,
                    machineType: MachineType): ExperimentResult = {
    val unbuiltGraph = scheduler.generateSchedule(numberOfNodes, machineType)
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
