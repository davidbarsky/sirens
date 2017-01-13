package com.davidbarsky.schedulers

import java.util.{List => JavaList}

import com.davidbarsky.dag.models.TaskQueue

trait BoundedScheduler {
  def generateSchedule(numQueues: Int): JavaList[TaskQueue]
}
