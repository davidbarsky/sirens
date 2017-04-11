package sirens

package object Data {
  type Cost = Int
  case class NetworkingBounds(lower: Cost, upper: Cost)
  case class LatencyBounds(lower: Cost, upper: Cost)
}
