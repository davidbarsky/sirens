package sirens.dag

import java.util
import sirens.models.TaskQueue
import collection.JavaConverters._

object CostAnalyzer {
  def findCostOfBuiltTasks(tqs: util.List[TaskQueue]): Int = {
    val graph = tqs.asScala.toList

    val hasUnbuilt = graph.exists(_.hasUnbuiltTask)
    if (hasUnbuilt) {
      throw new DAGException("TaskQueues have unbuilt tasks!")
    }

    graph.map { tq: TaskQueue =>
      (tq.getEndTime - tq.getStartTime) * tq.getMachineType.getCost
    }.sum
  }

  def getLatency(tqs: util.List[TaskQueue]): Int = {
    if (tqs == null) return Integer.MAX_VALUE
    tqs.stream.mapToInt((tq: TaskQueue) => tq.getEndTime + 60).max.getAsInt
  }
}
