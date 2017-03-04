package com.davidbarsky.schedulers

import java.util

import collection.JavaConverters._
import com.davidbarsky.dag.TopologicalSorter
import com.davidbarsky.dag.models.{Task, TaskQueue}
import com.davidbarsky.dag.models.states.MachineType
import com.davidbarsky.typeclasses.TaskExtension._

import collection.immutable.SortedSet

import scala.annotation.tailrec

// Since the LC algorithm does not schedule nodes on different paths
// to the same processor, it cannot guarantee optimal solutions
// for both fork and join structures.

case class Edge(task: Task, networkCost: Int) extends Ordered[Edge] {
  override def compare(that: Edge): Int =
    this.networkCost compare that.networkCost
}

case class Rose(task: Task, children: SortedSet[Rose]) extends Ordered[Rose] {
  override def compare(that: Rose): Int =
    this.task.getID compare that.task.getID

  def insert(newTask: Task): Rose = {
    this.copy(this.task, this.children + Rose(newTask, SortedSet[Rose]()))
  }
}

//case class Tree(value: Task,
//                parent: Option[Tree],
//                children: MutableSortedSet[Tree])
//    extends Ordered[Tree] {
//  def compare(that: Tree): Int =
//    this.value.getID compare that.value.getID
//}
//
//object TreeOps {
//  def getImmediateChildren(parent: Tree): MutableSortedSet[Tree] = {
//    val taskChildren = parent.value.getDependents.keySet().asScala.toList
//    val children = taskChildren.map(t => Tree(t, Some(parent), MutableSortedSet[Tree]()))
//    val mutableSortedSet = MutableSortedSet[Tree]()
//    children.foreach(t => mutableSortedSet.add(t))
//  }
//  def insert(task: Task, parent: Option[Tree]): Tree = {}
//}

// So here's the plan.
// First, split the graph into two groups: nodes *with* neighbors and *without*.
// The ones without neighbors are shuffled onto their own task queue. Those can be built without worry.
// That leaves us with the rest. Of the rest (the dependent nodes) need to scheduled.
// To do that, we will find the longest path between a source and leaf node. To accomplish this,
// we will use a BFS (with negative weights determine the "shortest" path).
// On each iteration of the BFS, we'll pass a set of visited nodes. We stop iteration when
// the set of visited nodes equals the size of the dependent nodes.

object LinearCluster extends UnboundedScheduler {
  override def generateSchedule(numNodes: Int): util.List[TaskQueue] = ???

  def test(): Unit = {
    val graph: List[Task] = TopologicalSorter.generateGraph(90).asScala.toList

    def isIndependent(task: Task) = task.isFreeNode

    val independentTasks: List[Task] = graph.filter(isIndependent)
    val dependentTasks: List[Task] = graph.filterNot(isIndependent)

    for (source <- graph) {
      val dependentCount = source.countDependents
      val allChildren = source.allChildren

      println(dependentCount, allChildren)
      if (dependentCount != allChildren.length) {
        println(source, dependentCount, allChildren)
        assert(dependentCount == allChildren.length)
      }
    }

    val independentQueue =
      new TaskQueue(MachineType.SMALL, independentTasks.asJava)
    independentQueue :: List[TaskQueue]()

    dependentTasks.foreach(t => longestPathByWeight(t, List[Edge]()))
  }

  def findPath(start: Task, end: Task): Unit = {
    def findPathHelper(current: Task,
                       end: Task,
                       path: List[Task]): Unit = {

    }

    val path = List[Task]()
    findPathHelper(start, end, path)
  }

  def reverseList(list: List[Task]): List[Task] = {
    list match {
      case Nil => Nil
      case head :: tail => head :: reverseList(tail)
    }
  }

  @tailrec
  def longestPathByWeight(task: Task, path: List[Edge]): List[Edge] = {
    val edges = task.getDependents.asScala.map {
      case (task: Task, cost: Integer) => Edge(task, cost)
    }.toList

    max(edges) match {
      case None => path.reverse
      case Some(edge) => longestPathByWeight(edge.task, edge :: path)
    }
  }

  @tailrec def max(as: List[Edge]): Option[Edge] = as match {
    case Nil => None
    case List(Edge(task: Task, cost: Int)) => Some(Edge(task, cost))
    case x :: y :: tail =>
      max((if (x.networkCost > y.networkCost) x else y) :: tail)
  }
}

//  // For the number of nodes we're working with, a DFS combined
//  // with a scheduling each path onto a TaskQueue appears to be sufficient.
//  def generateSchedule(numNodes: Int): util.List[TaskQueue] = {
//    val visited = new util.HashSet[Task]()
//    val paths = Buffer[TaskQueue]()
//
//    def recurse(node: Task, path: TaskQueue): Unit = {
//      if (visited.contains(node)) {
//        return
//      }
//      visited.add(node)
//      path.add(node)
//      if (node.isLeaf) {
//        paths += path
//      }
//
//      node.getDependents
//        .keySet()
//        .asScala
//        .foreach { t =>
//          recurse(t, path)
//        }
//    }
//
//    def scheduleFreeNodes(graph: util.List[Task]): TaskQueue = {
//      new TaskQueue(
//        MachineType.SMALL,
//        graph
//          .stream()
//          .filter(_.isFreeNode)
//          .collect(Collectors.toList())
//      )
//    }
//
//    val graph = TopologicalSorter.generateGraph(numNodes).asScala.toList
//
////    graph.stream().filter(_.isSource).forEach { t =>
////      recurse(t, new TaskQueue(MachineType.SMALL))
////    }
////    paths += scheduleFreeNodes(graph)
//
//    paths.foreach { tq =>
//      tq.getTasks.asScala
//        .sortWith(_.getID < _.getID)
//        .asJava
//    }
//
//    paths.asJava
//  }
//
//  // Must take a topologically sorted list.
//  def allPaths(graph: Seq[Task]): Unit = {
//    def findPaths(g: Seq[Task], t: Task): Unit = {
//
//      val pending = MutableQueue[List[Task]]()
//
//      pending.enqueue(List(t))
//      while (pending.nonEmpty) {
//        val l = pending.dequeue()
//        val lastTask = l.last
//
//        println(l)
//        lastTask.getNeighbors.asScala
//          .foreach { neighbor: Task =>
//            pending.enqueue(l ++ List(neighbor))
//          }
//      }
//    }
//    findPaths(graph, graph.head)
//  }
//}
