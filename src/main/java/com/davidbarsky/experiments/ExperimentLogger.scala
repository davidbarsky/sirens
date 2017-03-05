package com.davidbarsky.experiments

import java.util
import java.io.{File, FileWriter, PrintWriter}

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
