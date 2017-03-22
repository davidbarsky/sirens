package com.davidbarsky.schedulers

import java.util

import collection.JavaConverters._
import com.davidbarsky.dag.models.{Task, TaskQueue}
import com.davidbarsky.dag.models.states.MachineType
import com.davidbarsky.extensions.TaskExtension._

import scala.annotation.tailrec
import collection.mutable.{ListBuffer, Set => MutableSet}

// So here's the plan.
// First, split the graph into two groups: nodes *with* neighbors and *without*.
// The ones without neighbors are shuffled onto their own task queue. Those can be built without worry.
// That leaves us with the rest. Of the rest (the dependent nodes) need to scheduled.
// To do that, we will find the longest path between a source and leaf node. To accomplish this,
// we will use a BFS (with negative weights determine the "shortest" path).
// On each iteration of the BFS, we'll pass a set of visited nodes. We stop iteration when
// the set of visited nodes equals the size of the dependent nodes.

class LinearCluster extends UnboundedScheduler {
  override def generateSchedule(
      graph: util.List[Task],
      machineType: MachineType): util.List[TaskQueue] = {

    // Initialization
    val immutableGraph = graph.asScala.toList
    val independentTasks: List[Task] = immutableGraph.filter(_.isIndependent)
    val sourceTasks: List[Task] = immutableGraph.filter(_.isSource)
    val levels: Map[Task, Int] =
      GraphProperties.findBottomLevel(immutableGraph, machineType)
    val criticalPaths: List[List[Task]] =
      sourceTasks.map(t => findCriticalPath(t, levels))
    val adjacentNodes: List[List[Task]] =
      criticalPaths.map(neighborsOfCriticalPath)

    val examined = MutableSet[Task]()
    val buffer = ListBuffer[TaskQueue]()

    for (member <- adjacentNodes) {
      val candidates = member.filterNot(t => examined.contains(t)).sortWith(_.getID < _.getID)
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
