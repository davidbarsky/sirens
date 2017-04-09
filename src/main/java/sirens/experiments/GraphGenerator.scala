package sirens.experiments

import java.util

import sirens.dag.models.Task

import collection.JavaConverters._
import info.rmarcus.ggen4j.GGen
import sirens.dag.TopologicalSorter

// @formatter:off
object GraphGenerator {
  val latencyBounds: (Int, Int) = (10, 60)
  val networkingBounds: (Int, Int) = (10, 60)

  def genericGraph(graphSize: Int): util.List[Task] = {
    val graph = GGen.staticGraph().forkJoin(graphSize, 8)
      .vertexProperty("latency").uniform(latencyBounds._1, latencyBounds._2)
      .edgeProperty("networking").uniform(networkingBounds._1, networkingBounds._2)
      .generateGraph().topoSort()

    TopologicalSorter.mapToTaskList(graph.allVertices())
  }

  def cholesky: util.List[util.List[Task]] = {
    (10 :: 12 :: 14 :: 16 :: 18 :: 20 :: Nil).map { n =>
      val graph = GGen.dataflowGraph().cholesky(n)
        .vertexProperty("latency").uniform(latencyBounds._1, latencyBounds._2)
        .edgeProperty("networking").uniform(networkingBounds._1, networkingBounds._2)
        .generateGraph().topoSort()
      TopologicalSorter.mapToTaskList(graph.allVertices())
    }.asJava
  }

  def fibonacci: util.List[util.List[Task]] = {
    (5 :: 6 :: 7 :: 8 :: 9 :: 10 :: Nil).map { n =>
      val graph = GGen.staticGraph().fibonacci(n, 1)
        .vertexProperty("latency").uniform(latencyBounds._1, latencyBounds._2)
        .edgeProperty("networking").uniform(networkingBounds._1, networkingBounds._2)
        .generateGraph().topoSort()

      TopologicalSorter.mapToTaskList(graph.allVertices())
    }.asJava
  }

  def forkJoin: util.List[util.List[Task]] = {
    (2 :: 3 ::4 :: 5 ::6 ::7 :: Nil).map { n =>
      // `15` is the diameter of the graph
      val graph = GGen.staticGraph().forkJoin(n, 15)
        .vertexProperty("latency").uniform(latencyBounds._1, latencyBounds._2)
        .edgeProperty("networking").uniform(networkingBounds._1, networkingBounds._2)
        .generateGraph().topoSort()

      TopologicalSorter.mapToTaskList(graph.allVertices())
    }.asJava
  }

  def poisson: util.List[util.List[Task]] = {
    (6 :: 7 :: 8 :: 9 :: 10 :: 11 :: 12 :: 13 :: Nil).map { n =>
      val graph = GGen.dataflowGraph().poisson2D(20, n)
        .vertexProperty("latency").uniform(latencyBounds._1, latencyBounds._2)
        .edgeProperty("networking").uniform(networkingBounds._1, networkingBounds._2)
        .generateGraph().topoSort()

      TopologicalSorter.mapToTaskList(graph.allVertices())
    }.asJava
  }

  def sparseLU: util.List[util.List[Task]] = {
    (7 :: 8 :: 9 :: 10 :: 11 :: 12 :: Nil).map { n =>
      val graph = GGen.dataflowGraph().sparseLU(n)
        .vertexProperty("latency").uniform(latencyBounds._1, latencyBounds._2)
        .edgeProperty("networking").uniform(networkingBounds._1, networkingBounds._2)
        .generateGraph().topoSort()

      TopologicalSorter.mapToTaskList(graph.allVertices())
    }.asJava
  }

  def erdos: util.List[util.List[Task]] = {
    (20 :: 220 :: 364 :: 560 :: 816 :: 1140 :: 1540 :: Nil).map { n =>
      val graph = GGen.generateGraph().erdosGNM(n, 100)
        .vertexProperty("latency").uniform(latencyBounds._1, latencyBounds._2)
        .edgeProperty("networking").uniform(networkingBounds._1, networkingBounds._2)
        .generateGraph().topoSort()
      TopologicalSorter.mapToTaskList(graph.allVertices())
    }.asJava
  }

  def erdosGNP: util.List[util.List[Task]] = {
    (220 :: 364 :: 560 :: 816 :: 1140 :: 1540 :: Nil).map { n =>
      val graph = GGen.generateGraph().erdosGNP(n, 0.5)
        .vertexProperty("latency").uniform(latencyBounds._1, latencyBounds._2)
        .edgeProperty("networking").uniform(networkingBounds._1, networkingBounds._2)
        .generateGraph().topoSort()
      TopologicalSorter.mapToTaskList(graph.allVertices())
    }.asJava
  }
}
