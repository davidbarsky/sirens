package quick.sirens.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import java.util
import sirens.experiments.GraphGenerator
import org.junit.Test
import sirens.dag.DAGException
import sirens.models.Task
import sirens.models.TaskQueue
import sirens.models.states.MachineType

class TaskTest {
  @Test def testAddDependency(): Unit = {
    val firstTask = new Task(1, new TaskQueue(MachineType.SMALL), new util.HashMap[MachineType, Integer])
    val secondTask = new Task(2, new TaskQueue(MachineType.SMALL), new util.HashMap[MachineType, Integer])

    firstTask.addDependency(5, secondTask)

    assertTrue(firstTask.getDependencies.containsKey(secondTask))
    assertTrue(secondTask.getDependents.containsKey(firstTask))
    assertEquals(firstTask.getDependencies.get(secondTask), new Integer(5))
    assertEquals(secondTask.getDependents.get(firstTask), new Integer(5))
  }

  @Test def buildable1(): Unit = {
    val firstTask = new Task(1, new TaskQueue(MachineType.SMALL), new util.HashMap[MachineType, Integer])
    assertTrue(firstTask.buildable)
  }

  @Test def buildable2(): Unit = {
    val firstTask = new Task(1, new TaskQueue(MachineType.SMALL), new util.HashMap[MachineType, Integer])
    val secondTask = new Task(2, new TaskQueue(MachineType.SMALL), new util.HashMap[MachineType, Integer])
    firstTask.addDependency(5, secondTask)
    assertFalse(firstTask.buildable)
  }

  @Test def testHashCode(): Unit = {
    val task = new Task(1, new TaskQueue(MachineType.LARGE), new util.HashMap[MachineType, Integer])
    assertEquals(task.hashCode, Integer.hashCode(1))
  }

  @Test(expected = classOf[DAGException]) def compareToWithUnbuiltTasks(): Unit = {
    val firstTask = new Task(1, new TaskQueue(MachineType.LARGE), new util.HashMap[MachineType, Integer])
    val secondTask = new Task(2, new TaskQueue(MachineType.LARGE), new util.HashMap[MachineType, Integer])
    firstTask.compareTo(secondTask)
  }

  @Test def isLeaf(): Unit = {
    val task = new Task(1, new TaskQueue(MachineType.LARGE), new util.HashMap[MachineType, Integer])
    assertTrue(task.isLeaf)
  }

  @Test def isSource(): Unit = {
    val task = new Task(1, new TaskQueue(MachineType.LARGE), new util.HashMap[MachineType, Integer])
    assertTrue(task.isSource)
  }

  @Test def isIndependent(): Unit = {
    val graph = GraphGenerator.genericGraph(30)
    val independentTaskSize = graph.stream.filter(task => task.isIndependent).count
    val dependentTaskSize = graph.stream.filter((task: Task) => !task.isIndependent).count

    assertEquals(graph.size, independentTaskSize + dependentTaskSize)
  }
}
