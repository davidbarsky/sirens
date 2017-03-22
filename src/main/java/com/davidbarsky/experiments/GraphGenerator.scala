package com.davidbarsky.experiments

import java.util

import com.davidbarsky.dag.models.Task

import collection.JavaConverters._
import com.davidbarsky.dag.{DAGGenerator, TopologicalSorter}

object GraphGenerator {

  def genericGraph(graphSize: Int): util.List[Task] = {
    val vertexGraph = DAGGenerator.getErdosGNMSources(graphSize)
    TopologicalSorter.mapToTaskList(vertexGraph)
  }

  def singleCholeskyGraph(size: Int): util.List[Task] = {
    val vertexGraph = DAGGenerator.getCholesky(size)
    TopologicalSorter.mapToTaskList(vertexGraph)
  }

  // Cholesky: 220, 364, 560, 816, 1140, 1540. Uses "blocks"
  def cholesky: util.List[util.List[Task]] = {
    (220 :: 364 :: 560 :: 816 :: 1140 :: 1540 :: Nil).map { n =>
      val vertexGraph = DAGGenerator.getCholesky(n)
      TopologicalSorter.mapToTaskList(vertexGraph)
    }.asJava
  }

  // Fibonacci: 15, 25, 41, 67, 109, 177
  def fibonacci: util.List[util.List[Task]] = {
    (6 :: 8 :: 10 :: 12 :: 14 :: 16 :: Nil).map { n =>
      val vertexGraph = DAGGenerator.getFibonacci(n)
      TopologicalSorter.mapToTaskList(vertexGraph)
    }.asJava
  }

  def erdos: util.List[util.List[Task]] = {
    (220 :: 364 :: 560 :: 816 :: 1140 :: 1540 :: Nil).map { n =>
      val vertexGraph = DAGGenerator.getErdosGNMSources(n)
      TopologicalSorter.mapToTaskList(vertexGraph)
    }.asJava
  }

  // Fork/Join: 35, 52, 69, 86, 103, 120
  def forkJoin: util.List[util.List[Task]] = {
    (35 :: 52 :: 69 :: 86 :: 103 :: 120 :: Nil).map { n =>
      val vertexGraph = DAGGenerator.getForkJoin(n)
      TopologicalSorter.mapToTaskList(vertexGraph)
    }.asJava
    (10 :: 15 :: 20 :: 25 :: 30 :: 35 :: Nil).map { n =>
      val vertexGraph = DAGGenerator.getForkJoin(n)
      TopologicalSorter.mapToTaskList(vertexGraph)
    }.asJava
  }

  // Poisson 2D: 288, 324, 360, 396, 432, 468
  def poisson: util.List[util.List[Task]] = {
    (288 :: 324 :: 360 :: 396 :: 432 :: 468 :: Nil).map { n =>
      val vertexGraph = DAGGenerator.getPoisson(n)
      TopologicalSorter.mapToTaskList(vertexGraph)
    }.asJava
  }

  // Sparse LU: 80, 84, 141, 145, 226, 230
  def sparseLU: util.List[util.List[Task]] = {
    (80 :: 84 :: 141 :: 145 :: 226 :: 230 :: Nil).map { n =>
      val vertexGraph = DAGGenerator.getSparseLU(n)
      TopologicalSorter.mapToTaskList(vertexGraph)
    }.asJava
  }
}
