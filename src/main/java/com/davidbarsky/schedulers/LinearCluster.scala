package com.davidbarsky.schedulers

import java.util

import collection.JavaConverters._
import com.davidbarsky.dag.models.{Task, TaskQueue}
import com.davidbarsky.dag.models.states.MachineType
import com.davidbarsky.extensions.TaskExtension._

import scala.annotation.tailrec
import collection.mutable.{ListBuffer, Set => MutableSet}

class LinearCluster extends UnboundedScheduler {
  override def generateSchedule(graph: util.List[Task],
                                machineType: MachineType): util.List[TaskQueue] = {
    val immutableGraph = graph.asScala.toList
    val independentTasks = immutableGraph.filter(_.isIndependent)
    val sourceTasks = immutableGraph.filter(_.isSource)
    val levels = GraphProperties.findBottomLevel(immutableGraph, machineType)

    val criticalPaths = sourceTasks.map(t => findCriticalPath(t, levels))
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

  def findCriticalPath(source: Task, levels: Map[Task, Int]): List[Task] = {
    @tailrec def go(t: Task, acc: List[Task]): List[Task] = {
      if (t.isLeaf) {
        acc.reverse
      } else {
        val maxChild = max(t.getChildren)
        go(maxChild, maxChild :: acc)
      }
    }

    @tailrec def max(as: List[Task]): Task = {
      as match {
        case List(t: Task) => t
        case a :: b :: rest =>
          max((if (levels(a) > levels(b)) a else b) :: rest)
      }
    }

    go(source, Nil)
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
