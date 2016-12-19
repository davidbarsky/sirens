package com.davidbarsky.schedulers

import com.davidbarsky.dag._
import com.davidbarsky.dag.models.{Task, TaskQueue}

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Set
import scala.collection.JavaConverters._

object UnboundedClusterPrototype {
  def linearCluster(): Unit = {
    val graph = DAGGenerator.getErdosGNMSources(20)
    val linearized: List[TaskQueue] = TopologicalSorter.invoke(graph).asScala.toList

    allPaths(linearized)
  }

  def allPaths(sources: List[TaskQueue]): Unit = {
    val visited = Set[Task]()

    def dfs(task: Task, paths: ListBuffer[Task]): Unit = {
      if (visited.contains(task)) {
        return
      }

      paths += task
      visited += task

      if (task.getDependencies.keySet().size() == 0) {
        println(paths)
      } else {
        task.getDependencies.keySet().forEach(child => dfs(child, paths))
      }
    }

    for (node: TaskQueue <- sources) {
      dfs(node.getTasks.get(0), ListBuffer[Task]())
    }
  }
}
