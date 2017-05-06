package info.rmarcus.dag.permsolve.optimal

import org.junit.Assert._
import org.junit.Test

class StarsAndBarsNodeTest {
  @Test def increasingTest(): Unit = {
    var sbn = new StarsAndBarsNode(10)
    assertTrue(sbn.partitionSizesIncreasing)

    sbn = sbn.getChild(2)
    assertTrue(sbn.partitionSizesIncreasing)

    sbn = sbn.getChild(3)
    assertTrue(sbn.partitionSizesIncreasing)
  }

  @Test def nonIncreasingTest(): Unit = {
    var sbn = new StarsAndBarsNode(10)
    assertTrue(sbn.partitionSizesIncreasing)

    sbn = sbn.getChild(2)
    assertTrue(sbn.partitionSizesIncreasing)

    sbn = sbn.getChild(1)
    assertFalse(sbn.partitionSizesIncreasing)
  }

  @Test def nonIncreasingLargeTest(): Unit = {
    var sbn = new StarsAndBarsNode(1000)
    assertTrue(sbn.partitionSizesIncreasing)

    sbn = sbn.getChild(10)
    assertTrue(sbn.partitionSizesIncreasing)

    sbn = sbn.getChild(20)
    assertTrue(sbn.partitionSizesIncreasing)

    sbn = sbn.getChild(30)
    assertTrue(sbn.partitionSizesIncreasing)

    sbn = sbn.getChild(5)
    assertFalse(sbn.partitionSizesIncreasing)
  }
}
