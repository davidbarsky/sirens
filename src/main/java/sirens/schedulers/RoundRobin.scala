package sirens.schedulers

import java.util
import scala.collection.JavaConverters._

import sirens.models.states.MachineType
import sirens.models.{Task, TaskQueue}

class RoundRobin extends BoundedScheduler {
  def generateSchedule(graph: util.List[Task],
                       numQueues: Int,
                       machineType: MachineType): util.List[TaskQueue] = {
    val immutableGraph = graph.asScala.toList
    val queues = Array.fill[TaskQueue](numQueues)(new TaskQueue(MachineType.SMALL))

    for ((task: Task, position: Int) <- immutableGraph.zipWithIndex) {
      val modulo = (position + 1) % numQueues
      queues(modulo).add(task)
    }

    queues.toSeq.asJava
  }

  override def toString: String = "RoundRobin"
}
