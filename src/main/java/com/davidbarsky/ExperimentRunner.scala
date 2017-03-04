package com.davidbarsky

import java.io.{File, FileWriter, PrintWriter}
import java.util

import collection.JavaConverters._

import com.davidbarsky.dag.{Actualizer, CostAnalyzer}
import com.davidbarsky.schedulers.{BoundedScheduler, UnboundedScheduler}

case class ExperimentResult(schedulerName: String,
                            numberOfNodes: Int,
                            numberOfQueues: Int,
                            finalCost: Int)

object ExperimentRunner {
  def runSeries(scheduler: UnboundedScheduler, startNodes: Int, endNodes: Int): util.List[ExperimentResult] = {
    (startNodes until endNodes).map { numNodes => runExperiment(scheduler, numNodes) }.asJava
  }

  def runSeries(scheduler: BoundedScheduler, startQueues: Int, endQueues: Int): util.List[ExperimentResult] = {
    (startQueues until endQueues).map { numQueues => runExperiment(scheduler, numQueues) }.asJava
  }

  def runExperiment(scheduler: UnboundedScheduler,
                    numberOfNodes: Int): ExperimentResult = {
    val unbuiltGraph = scheduler.generateSchedule(numberOfNodes)
    val builtGraph = Actualizer.actualize(unbuiltGraph)
    val cost = CostAnalyzer.findCostOfBuiltTasks(builtGraph)

    ExperimentResult(scheduler.getClass.toString, numberOfNodes, unbuiltGraph.size, cost)
  }

  def runExperiment(scheduler: BoundedScheduler,
                    numberOfQueues: Int): ExperimentResult = {
    val unbuiltGraph = scheduler.generateSchedule(numberOfQueues)
    val builtGraph = Actualizer.actualize(unbuiltGraph)
    val cost = CostAnalyzer.findCostOfBuiltTasks(builtGraph)

    ExperimentResult(scheduler.getClass.toString, builtGraph.size, numberOfQueues, cost)
  }
}

import purecsv.safe._
object ExperimentLogger {
  def toCSV(results: util.List[ExperimentResult]): String = {
    val stringBuilder = new StringBuilder()
    results.forEach(result => stringBuilder.append(result.toCSV(",") + "\n"))
    stringBuilder.toString
  }

  def writeToFile(results: util.List[ExperimentResult], file: File): Unit = {
    val printWriter = new PrintWriter(new FileWriter(file))
    printWriter.write(toCSV(results))
    printWriter.close()
  }
}
