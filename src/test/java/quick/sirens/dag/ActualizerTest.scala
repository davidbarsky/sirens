package quick.sirens.dag

import org.junit.Assert.assertTrue
import org.junit.Test

import collection.JavaConverters._

import sirens.dag.Actualizer
import sirens.experiments.GraphGenerator
import sirens.models.states.MachineType
import sirens.schedulers.RoundRobin

class ActualizerTest {

  @Test
  def actualize(): Unit = {
    val roundRobin = new RoundRobin
    val graph = GraphGenerator.genericGraph(20)
    val tqs = roundRobin.generateSchedule(graph, 4, MachineType.SMALL)
    val tasks = Actualizer.actualize(tqs).asScala.toList

    assertTrue(
      tasks
        .flatMap(_.getTasks.asScala.toList)
        .forall(_.isBuilt))
  }
}
