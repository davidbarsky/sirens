package com.davidbarsky.schedulers

import java.util.{List => JavaList}

import com.davidbarsky.dag.models.TaskQueue

trait Scheduler {
  def generateSchedule(numNodes: Int): JavaList[TaskQueue]
}
