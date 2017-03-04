package com.davidbarsky

import java.io.{BufferedWriter, File}

import purecsv.unsafe._
import com.davidbarsky.dag.{Actualizer, CostAnalyzer}
import com.davidbarsky.schedulers.{BoundedScheduler, UnboundedScheduler}

case class ExperimentResult(schedulerName: String,
                            numberOfNodes: Int,
                            numberOfQueues: Int,
                            finalCost: Int)

class ExperimentRunner(outputFile: File, bufferedWriter: BufferedWriter) {

  def runExperiment(scheduler: UnboundedScheduler,
                    numberOfNodes: Int): Unit = {
    val unbuiltGraph = scheduler.generateSchedule(numberOfNodes)
    val builtGraph = Actualizer.actualize(unbuiltGraph)
    val cost = CostAnalyzer.findCostOfBuiltTasks(builtGraph)

    writeResult(ExperimentResult(scheduler.getClass.toString, numberOfNodes, unbuiltGraph.size, cost)
  }
  def runExperiment(scheduler: BoundedScheduler,
                    numberOfQueues: Int): Unit = {
    val unbuiltGraph = scheduler.generateSchedule(numberOfQueues)
    val builtGraph = Actualizer.actualize(unbuiltGraph)
    val cost = CostAnalyzer.findCostOfBuiltTasks(builtGraph)

    writeResult(ExperimentResult(scheduler.getClass.toString, builtGraph.size, numberOfQueues, cost))
  }

  def writeResult(result: ExperimentResult): Unit = {
    val row = result.toCSV(",")
    bufferedWriter.append(row)
    bufferedWriter.newLine()
  }
}
