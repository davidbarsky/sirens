package sirens.schedulers

import java.util

import sirens.models.{Task, TaskQueue}
import sirens.models.states.MachineType

trait UnboundedScheduler {
  def generateSchedule(graph: util.List[Task], machineType: MachineType): util.List[TaskQueue]
}
