package com.davidbarsky.schedulers

import java.util
import java.util.Comparator
import java.util.stream.Collectors

import com.davidbarsky.dag.TopologicalSorter
import com.davidbarsky.dag.models.{Task, TaskQueue}

// Since the EZ algorithm considers only the communication costs among nodes
// to make scheduling decisions, it does not guarantee optimal schedules
// for both fork and join structures.
class EdgeZero extends UnboundedScheduler {
  override def generateSchedule(numNodes: Int): util.List[TaskQueue] = {
    // descending order of edge weight
    val byEdgeWeight: Comparator[Task] = (t1: Task, t2: Task) =>
      Integer.compare(t2.edgeWeight(), t1.edgeWeight())

    val graph: util.List[Task] = TopologicalSorter
      .generateGraph(numNodes)
      .stream()
      .sorted(byEdgeWeight)
      .collect(Collectors.toList[Task])
    val examinedTasks = new util.HashSet[Task]()

    new util.ArrayList[TaskQueue]()
  }
}
