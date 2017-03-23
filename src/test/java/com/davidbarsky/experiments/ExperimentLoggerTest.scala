package com.davidbarsky.experiments

import com.davidbarsky.dag.models.states.MachineType
import com.davidbarsky.schedulers.EdgeZero
import org.junit.Test
import java.io.File
import java.nio.file.Files
import org.junit.Assert._

class ExperimentLoggerTest {
  @Test
  @throws[Exception]
  def toCSV(): Unit = {
    val graph = GraphGenerator.genericGraph(8)
    val experimentResult: ExperimentResult =
      ExperimentRunner.runExperiment(new EdgeZero, graph.size, graph, MachineType.SMALL)
    val csv: String = ExperimentLogger.toCSV(experimentResult :: Nil)
    // We're checking the first parts of the CSV fit the pattern of "EdgeZero,SMALL". Numbers that follow
    // are non-deterministic
    assertEquals(csv.substring(0, 14), "EdgeZero,SMALL")
  }

  @Test
  @throws[Exception]
  def writeToFile(): Unit = {
    val graph = GraphGenerator.genericGraph(8)
    val experimentResult: ExperimentResult =
      ExperimentRunner.runExperiment(new EdgeZero, graph.size, graph, MachineType.SMALL)
    val testCSV: String = ExperimentLogger.toCSV(experimentResult :: Nil)

    val file: File = File.createTempFile("output", "txt")
    file.deleteOnExit()
    ExperimentLogger.writeToFile(experimentResult :: Nil, file)
    val testActual: String = String.join("\n", Files.readAllLines(file.toPath)) + "\n"
    assertEquals(testCSV, testActual)
  }
}
