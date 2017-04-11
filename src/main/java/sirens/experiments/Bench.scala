package sirens.experiments

import sirens.models.states.MachineType
import sirens.schedulers.LinearCluster

object Bench {
  def runExperiments(): Unit = {
    val graphs = GraphGenerator.cholesky
    val results = ExperimentRunner.runSeries(new LinearCluster(), graphs, MachineType.SMALL)
    results.foreach(println)
  }
}
