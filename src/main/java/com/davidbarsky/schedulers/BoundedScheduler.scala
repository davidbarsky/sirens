package com.davidbarsky.schedulers

import java.util

import com.davidbarsky.dag.models.TaskQueue
import com.davidbarsky.dag.models.states.MachineType

trait BoundedScheduler {
  def generateSchedule(numQueues: Int, machineType: MachineType): util.List[TaskQueue]
}
