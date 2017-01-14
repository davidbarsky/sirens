package com.davidbarsky.schedulers

import java.util

import collection.JavaConverters._
import collection.mutable
import com.davidbarsky.dag.TopologicalSorter
import com.davidbarsky.dag.models.{Task, TaskQueue}
import info.rmarcus.ggen4j.graph.Vertex

// Since the EZ algorithm considers only the communication costs among nodes
// to make scheduling decisions, it does not guarantee optimal schedules
// for both fork and join structures.
class EdgeZero extends UnboundedScheduler {
  override def generateSchedule(numNodes: Int): util.List[TaskQueue] = {

    // We generate the graph, and sort it by the node's edge weight
    // in descending order.
    val graph: util.List[Task] = TopologicalSorter.generateGraph(numNodes)

    val visited = mutable.Set[Task]()
    for (task: Task <- graph.asScala) {

    }

    new util.ArrayList[TaskQueue]()
  }

  // So far, cost of all tasks is either 1 or 2, depending if they're scheduled
  // on a small/large machine. Since we don't know the *cost* of a task until its scheduled
  // on a machine, tLevel  & bLevel will assume MachineType.SMALL, with a default value of 1.
  def findTLevel(graph: util.List[Task], computationCost: Int = 1): util.Map[Task, Integer] = {
    val levels = new util.HashMap[Task, Integer]()
    for (task: Task <- graph.asScala) {
      var max = 0
      for (parent: Task <- task.getDependencies.keySet().asScala) {
        if (levels.getOrDefault(parent, 0) + computationCost + parent.getCostTo(task) > max) {
          max = levels.getOrDefault(parent, 0) + computationCost + parent.getCostTo(task)
        }
      }
      levels.put(task, max)
    }

    levels
  }

  def findBLevel(graph: util.List[Task], computationCost: Int = 1): util.Map[Task, Integer] = {
    val levels = new util.HashMap[Task, Integer]()
    for (task: Task <- graph.asScala) {
      var max = 0
      for (child: Task <- task.getDependents.keySet().asScala) {
        if (levels.getOrDefault(child, 0) + computationCost + child.getCostTo(task) > max) {
          max = levels.getOrDefault(child, 0) + computationCost + child.getCostTo(task)
        }
      }
      levels.put(task, max)
    }

    levels
  }
}
