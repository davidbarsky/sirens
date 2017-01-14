package com.davidbarsky.schedulers;

import com.davidbarsky.dag.models.TaskQueue;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class RoundRobinTest {
  @Test
  public void generateSchedule() {
    BoundedScheduler roundRobin = new RoundRobin();
    List<TaskQueue> queues = roundRobin.generateSchedule(20);
    assertEquals(20, queues.size());
  }
}
