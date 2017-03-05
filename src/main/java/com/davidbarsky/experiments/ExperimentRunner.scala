package com.davidbarsky.experiments

import java.util

import com.davidbarsky.dag.{Actualizer, CostAnalyzer}
import com.davidbarsky.schedulers.{BoundedScheduler, UnboundedScheduler}

import scala.collection.JavaConverters._

object ExperimentRunner {
  def runSeries(scheduler: UnboundedScheduler,
                startNodes: Int,
                endNodes: Int): util.List[ExperimentResult] = {
    (startNodes until endNodes).map { numNodes =>
      runExperiment(scheduler, numNodes)
    }.asJava
  }

  def runSeries(scheduler: BoundedScheduler,
                startQueues: Int,
                endQueues: Int): util.List[ExperimentResult] = {
    (startQueues until endQueues).map { numQueues =>
      runExperiment(scheduler, numQueues)
    }.asJava
  }

  def runExperiment(scheduler: UnboundedScheduler,
                    numberOfNodes: Int): ExperimentResult = {
    val unbuiltGraph = scheduler.generateSchedule(numberOfNodes)
    val builtGraph = Actualizer.actualize(unbuiltGraph)
    val cost = CostAnalyzer.findCostOfBuiltTasks(builtGraph)

    ExperimentResult(scheduler.getClass.toString,
                     numberOfNodes,
                     unbuiltGraph.size,
                     cost)
  }

  def runExperiment(scheduler: BoundedScheduler,
                    numberOfQueues: Int): ExperimentResult = {
    val unbuiltGraph = scheduler.generateSchedule(numberOfQueues)
    val builtGraph = Actualizer.actualize(unbuiltGraph)
    val cost = CostAnalyzer.findCostOfBuiltTasks(builtGraph)

    ExperimentResult(scheduler.getClass.toString,
                     builtGraph.size,
                     numberOfQueues,
                     cost)
  }
}
