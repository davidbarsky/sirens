package com.davidbarsky.schedulers

import java.util
import java.util.stream.Collectors

import collection.JavaConverters._
import com.davidbarsky.dag.TopologicalSorter
import com.davidbarsky.dag.models.{Task, TaskQueue}
import com.davidbarsky.dag.models.states.MachineType

import scala.collection.mutable.{ListBuffer => Buffer, Queue => MutableQueue}

// Since the LC algorithm does not schedule nodes on different paths
// to the same processor, it cannot guarantee optimal solutions
// for both fork and join structures.

case class Edge(task: Task, networkCost: Int) extends Ordered[Edge] {
  override def compare(that: Edge): Int =
    this.networkCost compare that.networkCost
}
class LinearCluster extends UnboundedScheduler {

  def test(): Unit = {
    val graph: List[Task] =
      TopologicalSorter.generateGraph(30).asScala.toList

    def isIndependent(task: Task) = task.isFreeNode
    val independentTasks: List[Task] = graph.filter(isIndependent)
    val dependentTasks: List[Task] = graph.filterNot(isIndependent)

    println("Dependent Tasks: " + dependentTasks.size)

    val allPaths = dependentTasks
      .filter(_.isSource)
      .flatMap(t => findPaths(t, List[Edge]()))
      .size

    println("Build Edges: " + allPaths)

    val independentQueue =
      new TaskQueue(MachineType.SMALL, independentTasks.asJava)
    independentQueue :: List[TaskQueue]()

    longestPath(dependentTasks)
  }

  def max(as: List[Edge]): Option[Edge] = as match {
    case Nil => None
    case List(Edge(task: Task, cost: Int)) => Some(Edge(task, cost))
    case x :: y :: rest =>
      max((if (x.networkCost > y.networkCost) x else y) :: rest)
  }

  def longestPath(graph: List[Task]): Unit = {
    graph.filterNot(_.isLeaf).foreach { t: Task =>
      val edges = t.getDependents.asScala.map {
        case (task: Task, cost: Integer) => Edge(task, cost)
      }.toList

      println(max(edges))
    }
  }

  def depth(task: Task): Int = {
    val edges = task.getDependents.asScala.map {
      case (task: Task, cost: Integer) => Edge(task, cost)
    }.toList

    max(edges) match {
      case None => 0
      case Some(topEdge) => 1 + topEdge.networkCost + depth(topEdge.task)
    }
  }

  def findPaths(task: Task, path: List[Edge]): List[Edge] = {
    val edges = task.getDependents.asScala.map {
      case (task: Task, cost: Integer) => Edge(task, cost)
    }.toList

    max(edges) match {
      case None => path.reverse
      case Some(edge) => findPaths(edge.task, edge :: path)
    }
  }

  // For the number of nodes we're working with, a DFS combined
  // with a scheduling each path onto a TaskQueue appears to be sufficient.
  def generateSchedule(numNodes: Int): util.List[TaskQueue] = {
    val visited = new util.HashSet[Task]()
    val paths = Buffer[TaskQueue]()

    def recurse(node: Task, path: TaskQueue): Unit = {
      if (visited.contains(node)) {
        return
      }
      visited.add(node)
      path.add(node)
      if (node.isLeaf) {
        paths += path
      }

      node.getDependents
        .keySet()
        .asScala
        .foreach { t =>
          recurse(t, path)
        }
    }

    def scheduleFreeNodes(graph: util.List[Task]): TaskQueue = {
      new TaskQueue(
        MachineType.SMALL,
        graph
          .stream()
          .filter(_.isFreeNode)
          .collect(Collectors.toList())
      )
    }

    val graph = TopologicalSorter.generateGraph(numNodes).asScala.toList

//    graph.stream().filter(_.isSource).forEach { t =>
//      recurse(t, new TaskQueue(MachineType.SMALL))
//    }
//    paths += scheduleFreeNodes(graph)

    paths.foreach { tq =>
      tq.getTasks.asScala
        .sortWith(_.getID < _.getID)
        .asJava
    }

    paths.asJava
  }

  // Must take a topologically sorted list.
  def allPaths(graph: Seq[Task]): Unit = {
    def findPaths(g: Seq[Task], t: Task): Unit = {

      val pending = MutableQueue[List[Task]]()

      pending.enqueue(List(t))
      while (pending.nonEmpty) {
        val l = pending.dequeue()
        val lastTask = l.last

        println(l)
        lastTask.getNeighbors.asScala
          .foreach { neighbor: Task =>
            pending.enqueue(l ++ List(neighbor))
          }
      }
    }
    findPaths(graph, graph.head)
  }
}
