package quick.sirens.models

import org.junit.Test
import sirens.models.StartEndTime

class StartEndTimeTest {
  @Test
  def getStart(): Unit = {
    val startEndTime = new StartEndTime(0, 10, 5)

    assert(0 == startEndTime.getStart)
  }

  @Test
  def getEnd(): Unit = {
    val startEndTime = new StartEndTime(0, 10, 5)

    assert(10 == startEndTime.getEnd)
  }

  @Test
  def getNetworkingStart(): Unit = {
    val startEndTime = new StartEndTime(0, 10, 5)

    assert(5 == startEndTime.getNetworkingStart)
  }
}
