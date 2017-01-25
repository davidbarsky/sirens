import com.davidbarsky.dag.models.states.MachineType
import com.davidbarsky.dag.models.{Task, TaskQueue}

import scala.annotation.tailrec
import scala.collection.JavaConverters._
import java.util

import com.davidbarsky.dag.{DAGGenerator, TopologicalSorter}

def tsort[A](edges: Traversable[(A, A)]): Iterable[A] = {
  @tailrec
  def tsort(toPreds: Map[A, Set[A]], done: Iterable[A]): Iterable[A] = {
    val (noPreds, hasPreds) = toPreds.partition { _._2.isEmpty }
    if (noPreds.isEmpty) {
      if (hasPreds.isEmpty) done else sys.error(hasPreds.toString)
    } else {
      val found = noPreds.map { _._1 }
      tsort(hasPreds.mapValues { _ -- found }, done ++ found)
    }
  }

  val toPred = edges.foldLeft(Map[A, Set[A]]()) { (acc, e) =>
    acc + (e._1 -> acc.getOrElse(e._1, Set())) + (e._2 -> (acc.getOrElse(e._2, Set()) + e._1))
  }
  tsort(toPred, Seq())
}

def tsort(taskQueue: TaskQueue): TaskQueue = {
  @tailrec
  def tsort(toPreds: Map[Task, Set[Task]], done: List[Task]): List[Task] = {
    val (noPreceding, hasPreceding) = toPreds.partition { _._2.isEmpty }
    if (noPreceding.isEmpty) {
      if (hasPreceding.isEmpty) done else sys.error(hasPreceding.toString)
    } else {
      val found = noPreceding.map { _._1 }
      tsort(hasPreceding.mapValues { _ -- found }, done ++ found)
    }
  }

  val toPred = {
    val map = new util.HashMap[Task, Set[Task]]()
    for (task <- taskQueue.getTasks.asScala) {
      val set = (task.getDependents.keySet().asScala ++ task.getDependencies.keySet().asScala).toSet
      map.put(task, set)
    }
    map.asScala
  }

  val result = tsort(toPred.toMap, List[Task]())
  val tq = new TaskQueue(MachineType.SMALL)
  result.foreach(t => tq.add(t))
  tq
}

val graph = TopologicalSorter.generateGraph(20)

tsort(Seq((1, 2), (2, 4), (3, 4), (3, 2), (1,3)))
