package sirens.experiments

import sirens.models.states.MachineType
import sirens.models.data._

case class ExperimentResult(schedulerName: String,
                            machineType: MachineType,
                            numberOfNodes: Int,
                            numberOfQueues: Int,
                            networkingBounds: NetworkingBounds,
                            latencyBounds: LatencyBounds,
                            finalCost: Int) {
  def toCSV: String = {
    s"$schedulerName,$machineType,$numberOfNodes,$numberOfQueues,$networkingBounds,$latencyBounds,$finalCost"
  }
}
