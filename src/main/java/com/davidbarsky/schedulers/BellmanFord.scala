package com.davidbarsky.schedulers

import com.davidbarsky.dag.models.Task
import com.davidbarsky.typeclasses.TaskExtension._

object BellmanFord {
  def findLongestPath(start: Task, visited: Set[Task]): Unit = {
    val children = start.getNegatativeDependencies
    println(children)
  }
}
