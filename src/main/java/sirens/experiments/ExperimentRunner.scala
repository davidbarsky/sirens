package sirens.experiments

import java.util

import info.rmarcus.dag.cca.CCAScheduler
import sirens.models.Task
import sirens.dag.CostAnalyzer
import sirens.dag.Actualizer
import sirens.models.states.MachineType
import sirens.models.data.{LatencyBounds, NetworkingBounds}
import sirens.schedulers.{BoundedScheduler, UnboundedScheduler}

import collection.JavaConverters._

object ExperimentRunner {
  def runSeries(scheduler: UnboundedScheduler,
                graphs: util.List[util.List[Task]],
                machineType: MachineType,
                networkingBounds: NetworkingBounds,
                latencyBounds: LatencyBounds): List[ExperimentResult] = {
    graphs.asScala.toList.map { graph =>
      runExperiment(
        scheduler = scheduler,
        numberOfNodes = graph.size,
        graph = graph,
        machineType = machineType,
        networkingBounds = networkingBounds,
        latencyBounds = latencyBounds
      )
    }
  }

  // Make this run a generic graph
  def runSeries(scheduler: BoundedScheduler,
                numberOfQueues: Int,
                graphs: util.List[util.List[Task]],
                machineType: MachineType,
                networkingBounds: NetworkingBounds,
                latencyBounds: LatencyBounds): List[ExperimentResult] = {
    graphs.asScala.toList.map { graph =>
      runExperiment(
        scheduler = scheduler,
        numberOfQueues = numberOfQueues,
        numberOfNodes = graph.size,
        graph = graph,
        machineType = machineType,
        networkingBounds = networkingBounds,
        latencyBounds = latencyBounds
      )
    }
  }

  def runExperiment(scheduler: UnboundedScheduler,
                    numberOfNodes: Int,
                    graph: util.List[Task],
                    machineType: MachineType,
                    networkingBounds: NetworkingBounds,
                    latencyBounds: LatencyBounds): ExperimentResult = {
    val unbuiltGraph = scheduler.generateSchedule(graph, machineType)
    val builtGraph = Actualizer.actualize(unbuiltGraph)
    val cost = CostAnalyzer.findCostOfBuiltTasks(builtGraph)

    ExperimentResult(
      schedulerName = scheduler.toString,
      machineType = machineType,
      numberOfNodes = numberOfNodes,
      numberOfQueues = builtGraph.size,
      networkingBounds = networkingBounds,
      latencyBounds = latencyBounds,
      finalCost = cost
    )
  }

  def runExperiment(scheduler: BoundedScheduler,
                    numberOfQueues: Int,
                    numberOfNodes: Int,
                    graph: util.List[Task],
                    machineType: MachineType,
                    networkingBounds: NetworkingBounds,
                    latencyBounds: LatencyBounds): ExperimentResult = {
    val unbuiltGraph = scheduler.generateSchedule(graph, numberOfQueues, machineType)
    val builtGraph = Actualizer.actualize(unbuiltGraph)
    val cost = CostAnalyzer.findCostOfBuiltTasks(builtGraph)

    ExperimentResult(
      schedulerName = scheduler.toString,
      machineType = machineType,
      numberOfNodes = numberOfNodes,
      numberOfQueues = numberOfQueues,
      networkingBounds = networkingBounds,
      latencyBounds = latencyBounds,
      finalCost = cost
    )
  }
}
