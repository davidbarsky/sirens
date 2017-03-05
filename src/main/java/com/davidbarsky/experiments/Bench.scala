package com.davidbarsky.experiments

import java.io.File

import com.davidbarsky.dag.models.states.MachineType
import com.davidbarsky.schedulers._

object Bench {
  def runExperiments(): Unit = {
    val ezResults = ExperimentRunner.runSeries(new EdgeZero(), 10, 100, MachineType.SMALL)
    val lcResults = ExperimentRunner.runSeries(new LinearCluster(), 10, 100, MachineType.SMALL)
    val rrResults = ExperimentRunner.runSeries(new RoundRobin(), 10, 100, MachineType.SMALL)

    val results = ezResults :: lcResults :: rrResults :: Nil
    results.foreach { result =>
      val tempFile =  File.createTempFile("temporaryFile", "txt")
      tempFile.deleteOnExit()

      ExperimentLogger.writeToFile(result, tempFile)
    }
  }
}
