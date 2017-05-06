package sirens.schedulers

import java.util

import collection.JavaConverters._
import sirens.typeclasses.TaskExtension._
import sirens.models.{Task, TaskQueue}
import sirens.models.states.MachineType

import scala.annotation.tailrec
import collection.mutable.{ListBuffer, Set => MutableSet}

class LinearCluster extends UnboundedScheduler {
  override def generateSchedule(graph: util.List[Task],
                                machineType: MachineType): util.List[TaskQueue] = {
    val immutableGraph = graph.asScala.toList
    val independentTasks = immutableGraph.filter(_.isIndependent)
    val sourceTasks = immutableGraph.filter(_.isSource)
    val levels = GraphProperties.findBottomLevel(immutableGraph, machineType)

    val criticalPaths = immutableGraph.map(t => GraphProperties.findCriticalPathWithLevels(t, levels))
    val adjacentNodes = criticalPaths.map(neighborsOfCriticalPath)

    val examined = MutableSet[Task]()
    val buffer = ListBuffer[TaskQueue]()

    for (member <- adjacentNodes) {
      val candidates =
        member.filterNot(t => examined.contains(t)).sortWith(_.getID < _.getID)
      buffer += new TaskQueue(machineType, candidates.asJava)
      candidates.foreach(t => examined.add(t))
    }
    buffer += new TaskQueue(machineType, independentTasks.asJava)

    buffer.filterNot(_.getTasks.isEmpty).asJava
  }

  def neighborsOfCriticalPath(criticalPath: List[Task]): List[Task] = {
    val path = MutableSet[Task]()
    for (member <- criticalPath) {
      val neighbors = member.getChildren ++ member.getParents
      neighbors.map(t => path.add(t))
    }
    path.toList.sortWith(_.getID < _.getID)
  }

  override def toString: String = {
    "LinearCluster"
  }
}
