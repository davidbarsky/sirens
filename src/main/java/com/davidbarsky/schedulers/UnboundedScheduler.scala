package com.davidbarsky.schedulers

import java.util

import com.davidbarsky.dag.models.{TaskQueue, Task}
import com.davidbarsky.dag.models.states.MachineType

trait UnboundedScheduler {
  def generateSchedule(graph: util.List[Task],
                       machineType: MachineType): util.List[TaskQueue]
}
