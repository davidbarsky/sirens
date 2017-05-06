package sirens.dag

import java.util
import java.util.stream.Collectors
import sirens.models.TaskQueue

import scala.collection.JavaConverters._

object Actualizer {
  def actualize(tqs: util.Collection[TaskQueue]): util.List[TaskQueue] = {
    tqs.forEach(_.unbuildAll())
    while (tqs.stream.anyMatch(_.hasUnbuiltTask)) {
      if (tqs.stream.noneMatch(_.buildNextUnbuiltTask)) {
        throw new DAGException(
          "Could not build task! Check input graph for cycles, and make sure all dependencies are in a task queue." + tqs)
      }
    }

    // check invariant: all tasks should now be built
    if (tqs.stream.anyMatch(_.hasUnbuiltTask)) {
      println(tqs.asScala.toList.map(_.toShortString))
      throw new DAGException(
        "Could not build task! Check input graph for cycles, and make sure all dependencies are in a task queue.")
    }
    new util.ArrayList[TaskQueue](tqs)
  }

  def invokeWithTopo(tqs: util.Collection[TaskQueue], topo: Array[Int]): util.List[TaskQueue] = {
    tqs.forEach(_.unbuildAll())

    val numTasks = tqs.stream.mapToInt((tq: TaskQueue) => tq.getTasks.size).sum

    // build a list of TaskQueues ordered by the topological order of their tasks. i.e. if
    // a taskqueue has task 1, 3, and 9, it should be in the list at positions 1, 3, and 9.
    val values = new Array[Int](numTasks)
    val unorderedTQs = new Array[TaskQueue](numTasks)
    val orderedTQs = new Array[TaskQueue](numTasks)

    var count = 0
    for (tq <- tqs.asScala.toList) {
      for (t <- tq.getTasks.asScala.toList) {
        values(count) = topo(t.getID)
        unorderedTQs(count) = tq
        count += 1
      }
    }

    for (i <- 0 until numTasks) {
      orderedTQs(values(i)) = unorderedTQs(i)
    }

    // now build the tasks in order
    for (tq <- orderedTQs) {
      if (!tq.buildNextUnbuiltTask) {
        throw new DAGException(
          "Could not build task! Check input graph for cycles, and make sure all dependencies are in a task queue.")
      }
    }
    if (tqs.stream.anyMatch(_.hasUnbuiltTask)) {
      System.out.println(tqs.asScala.toList.map(_.toShortString))
      throw new DAGException(
        "Could not build task! Check input graph for cycles, and make sure all dependencies are in a task queue.")
    }
    new util.ArrayList[TaskQueue](tqs)
  }
}
