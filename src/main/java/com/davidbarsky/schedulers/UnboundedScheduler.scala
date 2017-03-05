package com.davidbarsky.schedulers

import java.util

import com.davidbarsky.dag.models.TaskQueue
import com.davidbarsky.dag.models.states.MachineType

trait UnboundedScheduler {
  def generateSchedule(numNodes: Int, machineType: MachineType): util.List[TaskQueue]
}
