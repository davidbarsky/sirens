package sirens.typeclasses

import sirens.models.Task

import scala.collection.JavaConverters._

object TaskExtension {
  implicit object TaskOrdering extends Ordering[Task] {
    def compare(x: Task, y: Task): Int = x.getID compare y.getID
  }

  implicit class TaskExtension(task: Task) {
    def getNegatativeDependencies: Map[Task, Int] = {
      task.getDependencies.asScala.map { kv =>
        (kv._1, -kv._2)
      }.toMap
    }

    def getNegativeDependents: Map[Task, Int] = {
      task.getDependents.asScala.map { kv =>
        (kv._1, -kv._2)
      }.toMap
    }

    def getChildren: List[Task] = {
      task.getDependents.asScala.keys.toList
    }

    def getParents: List[Task] = {
      task.getDependencies.asScala.keys.toList
    }
  }
}
