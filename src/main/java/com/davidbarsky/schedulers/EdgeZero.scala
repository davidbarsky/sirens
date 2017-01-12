package com.davidbarsky.schedulers

import java.util
import collection.JavaConverters._
import collection.mutable

import com.davidbarsky.dag.TopologicalSorter
import com.davidbarsky.dag.models.{TaskQueue, Task}

// Since the EZ algorithm considers only the communication costs among nodes
// to make scheduling decisions, it does not guarantee optimal schedules
// for both fork and join structures.
class EdgeZero extends Scheduler {
  override def generateSchedule(numNodes: Int): util.List[TaskQueue] = {

    // We generate the graph, and sort it by the node's edge weight
    // in descending order.
    val graph = TopologicalSorter
      .generateGraph(numNodes)
      .asScala
      .sortWith(_.edgeWeight > _.edgeWeight)

    val visited = mutable.Set[Task]()
    for (task: Task <- graph) {

    }

    new util.ArrayList[TaskQueue]()
  }
}
