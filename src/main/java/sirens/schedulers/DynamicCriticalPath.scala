package sirens.schedulers
import java.util

import sirens.models.{Task, TaskQueue}
import sirens.models.states.MachineType
import sirens.typeclasses.TaskExtension._

import info.rmarcus.dynamic_critical_path.{DynamicCriticalPath => DCP}

import collection.JavaConverters._

object DynamicCriticalPath {

  type Edges = Map[Task, Map[Task, Int]]
  type Weights = Map[Task, Int]

  def generateSchedule(graph: util.List[Task], machineType: MachineType): Unit = {
    val (edges, weights) = transform(graph, machineType = MachineType.SMALL)
    val (stringyEdges, stringyWeights) = asJavaMaps(edges, weights)

    DCP.schedule(stringyEdges, stringyWeights)
  }

  def transform(graph: util.Collection[Task], machineType: MachineType): (Edges, Weights) = {
    val immutableGraph = graph.asScala.toList

    def nodeEdges(as: List[Task]): Edges = {
      as.foldLeft(Map[Task, Map[Task, Int]]()) { (map, task) =>
        map + (task -> task.getNeighbors)
      }
    }

    def nodeWeights(as: List[Task]): Weights = {
      as.map { task =>
          (task, (task.getLatencies.get(machineType)))
        }
        .foldLeft(Map[Task, Int]()) { (map, pair) =>
          val (task, weight) = pair
          map + (task -> weight)
        }
    }

    (nodeEdges(immutableGraph), nodeWeights(immutableGraph))
  }

  def asJavaMaps(edges: Edges, weights: Weights)
    : (util.Map[String, Integer], util.Map[String, util.Map[String, Integer]]) = {

    def edgesToString(edges: Edges): util.Map[String, util.Map[String, Integer]] = {
      edges.map { pair: (Task, Map[Task, Int]) =>
        val (task, map) = pair
        (task.toString, map.map { pair: (Task, Int) =>
          (pair._1.toString, new Integer(pair._2))
        }.asJava)
      }.asJava
    }

    def weightsToString(weights: Weights): util.Map[String, Integer] = {
      weights.map { pair: (Task, Int) =>
        (pair._1.toString, new Integer(pair._2))
      }.asJava
    }

    (weightsToString(weights), edgesToString(edges))
  }
}
