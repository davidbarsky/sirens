package com.davidbarsky.schedulers

import com.davidbarsky.dag.models.Task
import com.davidbarsky.typeclasses.TaskExtension._

object BellmanFord {

  sealed trait NodeDistance
  case class Reachable(cost: Int) extends NodeDistance
  case object Unreachable extends NodeDistance

  def findLongestPath(start: Task, visited: Set[Task]): Unit = {
    val children = start.getNegativeDependents
  }
}
