package com.davidbarsky.schedulers

import com.davidbarsky.dag.Actualizer
import com.davidbarsky.dag.models.Task
import com.davidbarsky.dag.models.TaskQueue
import com.davidbarsky.dag.models.states.BuildStatus
import com.davidbarsky.dag.models.states.MachineType
import com.davidbarsky.experiments.GraphGenerator
import org.junit.Test
import java.util
import org.junit.Assert._
import collection.JavaConverters._

class LinearClusterTest {
  @Test
  @throws[Exception]
  def generateSchedule() {
    val genericGraph: util.List[Task] = GraphGenerator.singleCholeskyGraph(10)
    val schedule: util.List[TaskQueue] =
      new LinearCluster().generateSchedule(genericGraph, MachineType.SMALL)

    val actualizedSchedule: List[TaskQueue] =
      Actualizer.actualize(schedule).asScala.toList

    assertTrue(actualizedSchedule.nonEmpty)
    assertNotNull(actualizedSchedule)

    assertTrue(
      actualizedSchedule
        .flatMap(_.getTasks.asScala.toList)
        .forall(_.getBuildStatus == BuildStatus.BUILT))
  }

  @Test
  @throws[Exception]
  def bLevelImpl() {
    val genericGraph: List[Task] =
      GraphGenerator.genericGraph(60).asScala.toList
    val linearCluster: LinearCluster = new LinearCluster
    val levels = GraphProperties.findBottomLevel(genericGraph, MachineType.SMALL)
    val sources = genericGraph.filter(_.isSource)
    println(sources)

    val paths = sources.map(t => linearCluster.findCriticalPath(t, levels))
    paths.map(linearCluster.neighborsOfCriticalPath).foreach(println)
  }
}
