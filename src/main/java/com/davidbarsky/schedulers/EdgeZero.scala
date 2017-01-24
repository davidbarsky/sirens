package com.davidbarsky.schedulers

import java.util
import java.util.Comparator
import java.util.stream.Collectors

import scala.collection.JavaConverters._
import com.davidbarsky.dag.{CostAnalyzer, TopologicalSorter}
import com.davidbarsky.dag.models.states.MachineType
import com.davidbarsky.dag.models.{Task, TaskQueue}

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.{Set => MutableSet}

// Since the EZ algorithm considers only the communication costs among nodes
// to make scheduling decisions, it does not guarantee optimal schedules
// for both fork and join structures.
class EdgeZero extends UnboundedScheduler {

  private val defaultMachineType = MachineType.SMALL

  private val byEdgeWeight: Comparator[Task] = (t1: Task, t2: Task) =>
    Integer.compare(t2.edgeWeight(), t1.edgeWeight())

  override def generateSchedule(numNodes: Int): util.List[TaskQueue] = {
    def clusterTaskToQueue(tasks: util.List[Task]): util.List[TaskQueue] = {
      val cluster = new util.ArrayList[TaskQueue](tasks.size())
      tasks.forEach { t: Task =>
        val taskQueue = new TaskQueue(defaultMachineType)
        taskQueue.add(t)
        cluster.add(taskQueue)
      }
      cluster
    }

    val graph: util.List[Task] = TopologicalSorter
      .generateGraph(numNodes)
      .stream()
      .sorted(byEdgeWeight)
      .collect(Collectors.toList[Task])

    val clusteredTasks = clusterTaskToQueue(graph)

    mergeClusters(clusteredTasks.asScala.toList)
      .filter(tq => tq.getTasks.size != 0)
      .asJava
  }

  def mergeClusters(as: List[TaskQueue]): List[TaskQueue] = {
    val visited = MutableSet[Task]()
    val buffer = ListBuffer[TaskQueue]()
    for (taskQueue: TaskQueue <- as) {
      val newTaskQueue = new TaskQueue(defaultMachineType)
      for (task: Task <- taskQueue.getTasks.asScala) {

        // First, we try to add the current task to the the TaskQueue
        if (!newTaskQueue.hasTask(task) && !visited.contains(task)) {
          newTaskQueue.add(task)
        }

        // Then, we find the unvisited, zeroable neighbors
        val neighbors = zeroableCandidates(task, as, visited)
        neighbors
          .filter { t: Task =>
            !newTaskQueue.hasTask(t)
          }
          .foreach { t: Task =>
            println("Adding task: " + t)
            newTaskQueue.add(t)
          }

        // Save visitation state
        visited.add(task)
        neighbors.foreach { t: Task =>
          visited.add(t)
        }
      }
      buffer += newTaskQueue
    }

    buffer.toList

//    as.view.zipWithIndex.foreach {
//      case (taskQueue: TaskQueue, index: Int) =>
//        val (_, after) = as.splitAt(index + 1)
//        val neighbors = taskQueue.getTasks.asScala
//          .flatMap(t => zeroableCandidates(t, as, visited))
//          .filterNot(t => visited.contains(t))
//
//        accumulator.addAll(taskQueue)
//
//        val newList: List[TaskQueue] = accumulator +: after
//
//        buffer += newList
//    }
//    buffer.toList
  }9

  private def zeroableCandidates(source: Task,
                                 rest: List[TaskQueue],
                                 visited: MutableSet[Task]): List[Task] = {
    val dependencies = source.getDependencies.keySet().asScala.toList
    val dependents = source.getDependents.keySet().asScala.toList

    val restOfTasks = rest.flatMap(_.getTasks.asScala)
    val neighbors = dependencies ++ dependents

    neighbors.intersect(restOfTasks).filter(t => !visited.contains(t))
  }

  def sortByEdgeWeight(dependency: Task): util.List[Task] = {
    dependency.getDependents
      .keySet()
      .stream()
      .sorted(byEdgeWeight)
      .collect(Collectors.toList[Task])
  }
}
