package com.davidbarsky.schedulers;

import com.davidbarsky.dag.models.TaskQueue;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RoundRobinTest {
    @Test
    void invoke() {
        ArrayList<TaskQueue> queues = RoundRobin.invoke(20);
        assertAll("Round Robin Queues",
                () -> assertEquals(20, queues.size()),
                () -> assertNotNull(queues)
        );
    }

}
