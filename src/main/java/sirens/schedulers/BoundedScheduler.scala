package sirens.schedulers

import java.util

import sirens.models.{Task, TaskQueue}
import sirens.models.states.MachineType

trait BoundedScheduler {
  def generateSchedule(graph: util.List[Task],
                       numQueues: Int,
                       machineType: MachineType): util.List[TaskQueue]
}
