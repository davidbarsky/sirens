package sirens.models

package object data {
  type Cost = Int
  case class NetworkingBounds(lower: Cost, upper: Cost)
  case class LatencyBounds(lower: Cost, upper: Cost)
}
