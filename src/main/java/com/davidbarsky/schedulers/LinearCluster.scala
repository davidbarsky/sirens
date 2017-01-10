package com.davidbarsky.schedulers

import collection.JavaConverters._
import java.util.{List => JavaList}

import com.davidbarsky.dag.TopologicalSorter
import com.davidbarsky.dag.models.{Task, TaskQueue}
import com.davidbarsky.dag.models.states.MachineType

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

// Since the LC algorithm does not schedule nodes on different paths
// to the same processor, it cannot guarantee optimal solutions
// for both fork and join structures.
class LinearCluster extends Scheduler {

  // For the number of nodes we're working with, a DFS combined
  // with a scheduling each path onto a TaskQueue appears to be sufficient.
  def generateSchedule(numNodes: Int): JavaList[TaskQueue] = {
    val visited = mutable.Set[Task]()
    val paths = ListBuffer[TaskQueue]()

    def recurse(node: Task, path: TaskQueue): Unit = {
      visited.add(node)
      path.add(node)
      if (node.isLeaf) {
        paths.append(path)
      }

      node.getDependents
        .keySet()
        .asScala
        .diff(visited)
        .foreach { t =>
          recurse(t, path)
        }
    }

    val graph = TopologicalSorter.generateGraph(numNodes).asScala
    graph.filter(_.isSource).foreach { t => recurse(t, new TaskQueue(MachineType.SMALL)) }

    paths.toList.asJava
  }

//  def linearCluster(numNodes: Int): util.List[TaskQueue] = {
//    val tasks = TopologicalSorter.generateGraph(numNodes)
//    tasks.asScala.filter(_.isSource).foreach(println)
//    val paths = ListBuffer[TaskQueue]()
//    var visited = mutable.Set[Task]()
//
//    def dfs(task: Task, path: TaskQueue): Unit = {
//      visited += task
//      path.add(task)
//
//      task.getDependents
//        .keySet()
//        .asScala
//        .foreach { t =>
//          dfs(t, path)
//        }
//    }
//
//    tasks.forEach { t =>
//      dfs(t, new TaskQueue(MachineType.SMALL))
//    }
//
//    paths.toList.asJava
//  }

//  def longestPath(numNodes: Int): Unit = {
//    val leaves = TopologicalSorter
//      .generateGraph(numNodes)
//      .asScala
//      .filter(_.isLeaf)
//      .toList
//
//    val result = leaves
//      .map { t =>
//        t.getDependencies.keySet.asScala.filter { l =>
//          l.outDegree() <= 1 && l.inDegree() <= 1
//        }.toList
//      }
//      .filterNot(_.isEmpty)
//
//    println(result)
//  }
}
