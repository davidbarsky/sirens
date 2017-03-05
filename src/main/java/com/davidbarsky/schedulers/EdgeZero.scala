package com.davidbarsky.schedulers

import java.util
import java.util.Comparator
import java.util.stream.Collectors

import scala.collection.JavaConverters._
import com.davidbarsky.dag.{Actualizer, CostAnalyzer, TopologicalSorter}
import com.davidbarsky.dag.models.states.MachineType
import com.davidbarsky.dag.models.{Task, TaskQueue}

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.{Set => MutableSet}

// Since the EZ algorithm considers only the communication costs among nodes
// to make scheduling decisions, it does not guarantee optimal schedules
// for both fork and join structures.
class EdgeZero extends UnboundedScheduler {

  private val byEdgeWeight: Comparator[Task] = (t1: Task, t2: Task) =>
    Integer.compare(t2.edgeWeight(), t1.edgeWeight())

  override def generateSchedule(numNodes: Int, machineType: MachineType): util.List[TaskQueue] = {
    def clusterTaskToQueue(tasks: util.List[Task]): util.List[TaskQueue] = {
      val cluster = new util.ArrayList[TaskQueue](tasks.size())
      tasks.forEach { t: Task =>
        val taskQueue = new TaskQueue(machineType)
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

    mergeClusters(clusteredTasks.asScala.toList, machineType).asJava
  }

  private def mergeClusters(as: List[TaskQueue], machineType: MachineType): List[TaskQueue] = {
    val visited = MutableSet[Task]()
    val buffer = ListBuffer[TaskQueue]()
    for ((taskQueue: TaskQueue, index: Int) <- as.view.zipWithIndex) {
      val newTaskQueue = new TaskQueue(machineType)
      for (task: Task <- taskQueue.getTasks.asScala) {
        // First, we try to add the current task to the the TaskQueue
        if (!newTaskQueue.hasTask(task) && !visited.contains(task)) {
          newTaskQueue.add(task)
        }

        // Then, we find the unvisited, zeroable neighbors
        val neighbors = findNearestNeighbors(task, as, visited)
        neighbors.foreach { t =>
          newTaskQueue.add(t)
        }

        // Save visitation state
        visited.add(task)
        neighbors.foreach { t =>
          visited.add(t)
        }
      }

      val sortedQueue: TaskQueue = topologicallySort(newTaskQueue, machineType)
//      val intermediate: List[TaskQueue] =
//        generateIntermediate(sortedQueue, as, buffer, index)
//      val cost = costIntermediateList(intermediate)
//      println(cost)

      buffer += sortedQueue
    }

    buffer.filter { tq =>
      tq.getTasks.size != 0
    }.toList
  }

  def generateIntermediate(sortedQueue: TaskQueue,
                           original: List[TaskQueue],
                           buffer: ListBuffer[TaskQueue],
                           currentIndex: Int): List[TaskQueue] = {
    val (_, after) = original.splitAt(currentIndex + 1)
    (buffer.clone() += sortedQueue).toList ++ after
  }

  def costIntermediateList(as: List[TaskQueue]): Int = {
    val builtGraph = Actualizer.actualize(as.asJava)
    CostAnalyzer.getLatency(builtGraph)
  }

  def findNearestNeighbors(source: Task,
                           rest: List[TaskQueue],
                           visited: MutableSet[Task]): List[Task] = {
    val dependencies = source.getDependencies.keySet().asScala.toList
    val dependents = source.getDependents.keySet().asScala.toList

    val restOfTasks = rest.flatMap(_.getTasks.asScala)
    val neighbors = dependencies ++ dependents

    restOfTasks
      .filter { t =>
        neighbors.contains(t)
      }
      .filter { t =>
        !visited.contains(t)
      }
  }

  def topologicallySort(taskQueue: TaskQueue, machineType: MachineType): TaskQueue = {
    val sortedList: util.List[Task] = taskQueue.getTasks.asScala
      .sortWith(_.getID < _.getID)
      .asJava

    new TaskQueue(machineType, sortedList)
  }

  override def toString: String = {
    "EdgeZero"
  }
}
