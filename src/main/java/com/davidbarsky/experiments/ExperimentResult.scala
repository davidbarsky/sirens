package com.davidbarsky.experiments

import com.davidbarsky.dag.models.states.MachineType

case class ExperimentResult(schedulerName: String,
                            machineType: MachineType,
                            numberOfNodes: Int,
                            numberOfQueues: Int,
                            finalCost: Int) {
  def toCSV: String = {
    schedulerName + "," + machineType + "," + numberOfNodes + "," + numberOfQueues + "," + finalCost + "\n"
  }
}
