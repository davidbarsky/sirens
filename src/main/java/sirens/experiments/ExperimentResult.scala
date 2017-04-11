package sirens.experiments

import sirens.models.states.MachineType

case class ExperimentResult(schedulerName: String,
                            machineType: MachineType,
                            numberOfNodes: Int,
                            numberOfQueues: Int,
                            finalCost: Int) {
  def toCSV: String = {
    schedulerName + "," + machineType + "," + numberOfNodes + "," + numberOfQueues + "," + finalCost + "\n"
  }
}
