package sirens.schedulers

import java.util

import sirens.dag.models.{Task, TaskQueue}
import sirens.dag.models.states.MachineType

trait UnboundedScheduler {
  def generateSchedule(graph: util.List[Task], machineType: MachineType): util.List[TaskQueue]
}
