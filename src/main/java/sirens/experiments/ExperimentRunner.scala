package sirens.experiments

import java.util

import sirens.dag.models.Task
import sirens.dag.CostAnalyzer
import sirens.schedulers.BoundedScheduler
import sirens.dag.Actualizer
import sirens.dag.models.states.MachineType
import sirens.schedulers.UnboundedScheduler

import collection.JavaConverters._

object ExperimentRunner {
  def runSeries(scheduler: UnboundedScheduler,
                graphs: util.List[util.List[Task]],
                machineType: MachineType): List[ExperimentResult] = {
    graphs.asScala.toList.map { graph =>
      runExperiment(scheduler, graph.size(), graph, machineType)
    }
  }

  // Make this run a generic graph
  def runSeries(scheduler: BoundedScheduler,
                numberOfQueues: Int,
                graphs: util.List[util.List[Task]],
                machineType: MachineType): List[ExperimentResult] = {
    graphs.asScala.toList.map { graph =>
      runExperiment(scheduler, numberOfQueues, graph, machineType)
    }
  }

  def runExperiment(scheduler: UnboundedScheduler,
                    numberOfNodes: Int,
                    graph: util.List[Task],
                    machineType: MachineType): ExperimentResult = {
    val unbuiltGraph = scheduler.generateSchedule(graph, machineType)
    val builtGraph = Actualizer.actualize(unbuiltGraph)
    val cost = CostAnalyzer.findCostOfBuiltTasks(builtGraph)

    ExperimentResult(scheduler.toString, machineType, numberOfNodes, unbuiltGraph.size, cost)
  }

  def runExperiment(scheduler: BoundedScheduler,
                    numberOfQueues: Int,
                    graph: util.List[Task],
                    machineType: MachineType): ExperimentResult = {
    val unbuiltGraph = scheduler.generateSchedule(numberOfQueues, machineType)
    val builtGraph = Actualizer.actualize(unbuiltGraph)
    val cost = CostAnalyzer.findCostOfBuiltTasks(builtGraph)

    ExperimentResult(scheduler.toString, machineType, builtGraph.size, numberOfQueues, cost)
  }
}
