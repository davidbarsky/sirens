package com.davidbarsky.experiments

import java.util

import collection.JavaConverters._

import com.davidbarsky.dag.DAGGenerator
import info.rmarcus.ggen4j.graph.Vertex

object GraphGenerator {

  // Cholesky: 220, 364, 560, 816, 1140, 1540. Uses "blocks"
  def cholesky: util.List[util.List[Vertex]] = {
    (220 :: 364 :: 560 :: 816 :: 1140 :: 1540 :: Nil).map { n =>
      DAGGenerator.getCholesky(n).asInstanceOf[util.List[Vertex]]
    }.asJava
  }

  // Fibonacci: 15, 25, 41, 67, 109, 177
  def fibonacci: util.List[util.List[Vertex]] = {
    (15 :: 25 :: 41 :: 67 :: 109 :: 177 :: Nil).map { n =>
      DAGGenerator.getFibonacci(n).asInstanceOf[util.List[Vertex]]
    }.asJava
  }

  // Fork/Join: 35, 52, 69, 86, 103, 120
  def forkJoin: util.List[util.List[Vertex]] = {
    (35 :: 52 :: 69 :: 86 :: 103 :: 120 :: Nil).map { n =>
      DAGGenerator.getForkJoin(n).asInstanceOf[util.List[Vertex]]
    }.asJava
  }

  // Poisson 2D: 288, 324, 360, 396, 432, 468
  def poisson: util.List[util.List[Vertex]] = {
    (288 :: 324 :: 360 :: 396 :: 432 :: 468 :: Nil).map { n =>
      DAGGenerator.getPoisson(n).asInstanceOf[util.List[Vertex]]
    }.asJava
  }

  // Sparse LU: 80, 84, 141, 145, 226, 230
  def sparseLU: util.List[util.List[Vertex]] = {
    (80 :: 84 :: 141 :: 145 :: 226 :: 230 :: Nil).map { n =>
      DAGGenerator.getSparseLU(n).asInstanceOf[util.List[Vertex]]
    }.asJava
  }
}
