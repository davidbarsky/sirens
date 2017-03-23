package com.davidbarsky.experiments

import java.util

import java.io.{File, FileWriter, PrintWriter}

object ExperimentLogger {
  def toCSV(results: List[ExperimentResult]): String = {
    val stringBuilder = new StringBuilder()
    results.foreach(result => stringBuilder.append(result.toCSV))
    stringBuilder.toString
  }

  def writeToFile(results: List[ExperimentResult], file: File): Unit = {
    val printWriter = new PrintWriter(new FileWriter(file))
    printWriter.write(toCSV(results))
    printWriter.close()
  }
}
