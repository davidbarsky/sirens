package slow.sirens.experiments

import org.junit.Test
import sirens.experiments.GraphGenerator
import sirens.models.states.MachineType
import sirens.schedulers.{DynamicCriticalPath, EdgeZero, LinearCluster, RoundRobin}

class BenchmarkErdos {
  @Test
  def dynamicCriticalPath(): Unit = {
    val sum = (1 until 20).map { i =>
      val start = System.currentTimeMillis()

      val graph = GraphGenerator.genericGraph(100)
      DynamicCriticalPath.generateSchedule(graph, machineType = MachineType.SMALL)

      val end = System.currentTimeMillis()
      println(s"Time to Schedule with Dynamic Critical Path: ${end - start} milliseconds")
      (end - start).toInt
    }.foldLeft(0)(_+_)

    val average = sum / 20
    println(s"Average Time for RoundRobin: ${average}")
  }

  @Test
  def linearCluster(): Unit = {
    val sum = (1 until 20).map { i =>
      val start = System.currentTimeMillis()

      val graph = GraphGenerator.genericGraph(100)
      val schedule = new LinearCluster().generateSchedule(graph, machineType = MachineType.SMALL)

      val end = System.currentTimeMillis()
      println(s"Time to Schedule with Linear Cluster: ${end - start} milliseconds")
      (end - start).toInt
    }.foldLeft(0)(_+_)

    val average = sum / 20
    println(s"Average Time for Round Robin: ${average}")
  }

  @Test
  def edgeZero(): Unit = {
    val sum = (1 until 20).map { i =>
      val start = System.currentTimeMillis()

      val graph = GraphGenerator.genericGraph(100)
      val schedule = new EdgeZero().generateSchedule(graph, machineType = MachineType.SMALL)

      val end = System.currentTimeMillis()
      println(s"Time to Schedule with Edge Zero: ${end - start} milliseconds")
      (end - start).toInt
    }.foldLeft(0)(_+_)

    val average = sum / 20
    println(s"Average Time for Edge Zero: ${average}")
  }

  @Test
  def roundRobin(): Unit = {
    val sum = (1 until 20).map { i =>
      val start = System.currentTimeMillis()

      val graph = GraphGenerator.genericGraph(100)
      val schedule = new RoundRobin().generateSchedule(graph, numQueues = 4, machineType = MachineType.SMALL)

      val end = System.currentTimeMillis()
      println(s"Time to Schedule with Round Robin: ${end - start} milliseconds")
      (end - start).toInt
    }.foldLeft(0)(_+_)

    val average = sum / 20
    println(s"Average Time for Round Robin: ${average}")
  }
}
