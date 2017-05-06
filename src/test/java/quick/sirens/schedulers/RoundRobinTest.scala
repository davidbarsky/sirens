package quick.sirens.schedulers

import org.junit.Assert.assertEquals
import sirens.experiments.GraphGenerator
import sirens.models.states.MachineType
import org.junit.Test
import sirens.schedulers.RoundRobin

class RoundRobinTest {
  @Test def invoke(): Unit = {
    val roundRobin = new RoundRobin
    val graph = GraphGenerator.genericGraph(20)
    val queues = roundRobin.generateSchedule(graph, 4, MachineType.SMALL)
    assertEquals(4, queues.size)
  }
}
