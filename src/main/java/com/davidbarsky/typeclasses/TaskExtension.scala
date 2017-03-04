package com.davidbarsky.typeclasses

import com.davidbarsky.dag.models.Task

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

object TaskExtension {
  implicit object TaskOrdering extends Ordering[Task] {
    def compare(x: Task, y: Task): Int = x.getID compare y.getID
  }

  implicit class TaskExtension(task: Task) {
    def getDependentsAsScala: List[Task] = {
      task.getDependents.keySet().asScala.toList
    }

    def allChildren: List[Task] = {
      val buffer = ListBuffer[Task]()
      def loop(t: Task): Unit = {
        if (!t.isLeaf) {
          buffer += t
          t.getDependentsAsScala.foreach(child => loop(child))
        }
      }
      loop(task)
      buffer.toList
    }

    def allChildrenF: List[Task] = {
      def loop(t: Task): List[Task] = {
        if (t.isLeaf || task == t) Nil
        else t.getDependentsAsScala.flatMap { child =>  t :: loop(child) }
      }
      loop(task)
    }

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
  }
}
