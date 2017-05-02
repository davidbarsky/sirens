package sirens.experiments

import java.util

import sirens.models.Task

import collection.JavaConverters._
import info.rmarcus.ggen4j.GGen
import sirens.dag.TopologicalSorter
import sirens.models.data.{LatencyBounds, NetworkingBounds}

// @formatter:off
object GraphGenerator {
  val latencyBounds: LatencyBounds = LatencyBounds(10, 60)
  val networkingBounds: NetworkingBounds = NetworkingBounds(10, 60)

  def genericGraph(graphSize: Int): util.List[Task] = {
    val graph = GGen.generateGraph().erdosGNM(graphSize, 100)
      .vertexProperty("latency").uniform(10, 60)
      .edgeProperty("networking").uniform(10, 60)
      .generateGraph().topoSort()
    TopologicalSorter.mapToTaskList(graph.allVertices())
  }

  def cholesky(latencyBounds: LatencyBounds, networkingBounds: NetworkingBounds): util.List[util.List[Task]] = {
    (10 :: 12 :: 14 :: 16 :: 18 :: 20 :: Nil).map { n =>
      val graph = GGen.dataflowGraph().cholesky(n)
        .vertexProperty("latency").uniform(latencyBounds.lower, latencyBounds.upper)
        .edgeProperty("networking").uniform(networkingBounds.lower, networkingBounds.upper)
        .generateGraph().topoSort()

      TopologicalSorter.mapToTaskList(graph.allVertices())
    }.asJava
  }

  def fibonacci(latencyBounds: LatencyBounds, networkingBounds: NetworkingBounds): util.List[util.List[Task]] = {
    (5 :: 6 :: 7 :: 8 :: 9 :: 10 :: Nil).map { n =>
      val graph = GGen.staticGraph().fibonacci(n, 1)
        .vertexProperty("latency").uniform(latencyBounds.lower, latencyBounds.upper)
        .edgeProperty("networking").uniform(networkingBounds.lower, networkingBounds.upper)
        .generateGraph().topoSort().allVertices()

      TopologicalSorter.mapToTaskList(graph)
    }.asJava
  }

  def forkJoin(latencyBounds: LatencyBounds, networkingBounds: NetworkingBounds): util.List[util.List[Task]] = {
    (2 :: 3 ::4 :: 5 ::6 ::7 :: Nil).map { n =>
      // `15` is the diameter of the graph
      val graph = GGen.staticGraph().forkJoin(n, 15)
        .vertexProperty("latency").uniform(latencyBounds.lower, latencyBounds.upper)
        .edgeProperty("networking").uniform(networkingBounds.upper, networkingBounds.upper)
        .generateGraph().topoSort()

      TopologicalSorter.mapToTaskList(graph.allVertices())
    }.asJava
  }

  def poisson(latencyBounds: LatencyBounds, networkingBounds: NetworkingBounds): util.List[util.List[Task]] = {
    (8 :: 9 :: 10 :: 11 :: 12 :: 13 :: Nil).map { n =>
      val graph = GGen.dataflowGraph().poisson2D(20, n)
        .vertexProperty("latency").uniform(latencyBounds.lower, latencyBounds.upper)
        .edgeProperty("networking").uniform(networkingBounds.lower, networkingBounds.upper)
        .generateGraph().topoSort()

      TopologicalSorter.mapToTaskList(graph.allVertices())
    }.asJava
  }

  def sparseLU(latencyBounds: LatencyBounds, networkingBounds: NetworkingBounds): util.List[util.List[Task]] = {
    (7 :: 8 :: 9 :: 10 :: 11 :: 12 :: Nil).map { n =>
      val graph = GGen.dataflowGraph().sparseLU(n)
        .vertexProperty("latency").uniform(latencyBounds.lower, latencyBounds.upper)
        .edgeProperty("networking").uniform(networkingBounds.lower, networkingBounds.upper)
        .generateGraph().topoSort()

      TopologicalSorter.mapToTaskList(graph.allVertices())
    }.asJava
  }

  def erdos(latencyBounds: LatencyBounds, networkingBounds: NetworkingBounds): util.List[util.List[Task]] = {
    (220 :: 364 :: 560 :: 816 :: 1140 :: 1540 :: Nil).map { n =>
      val graph = GGen.generateGraph().erdosGNM(n, 100)
        .vertexProperty("latency").uniform(latencyBounds.lower, latencyBounds.upper)
        .edgeProperty("networking").uniform(networkingBounds.lower, networkingBounds.upper)
        .generateGraph().topoSort()
      TopologicalSorter.mapToTaskList(graph.allVertices())
    }.asJava
  }

  def erdosGNP(latencyBounds: LatencyBounds, networkingBounds: NetworkingBounds): util.List[util.List[Task]] = {
    (220 :: 364 :: 560 :: 816 :: 1140 :: 1540 :: Nil).map { n =>
      val graph = GGen.generateGraph().erdosGNP(n, 0.5)
        .vertexProperty("latency").uniform(latencyBounds.lower, latencyBounds.upper)
        .edgeProperty("networking").uniform(networkingBounds.lower, networkingBounds.upper)
        .generateGraph().topoSort()
      TopologicalSorter.mapToTaskList(graph.allVertices())
    }.asJava
  }
}
