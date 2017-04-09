package sirens.schedulers

import java.util

import sirens.dag.models.TaskQueue
import sirens.dag.models.states.MachineType

trait BoundedScheduler {
  def generateSchedule(numQueues: Int, machineType: MachineType): util.List[TaskQueue]
}
