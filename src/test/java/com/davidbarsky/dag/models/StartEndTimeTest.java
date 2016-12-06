package com.davidbarsky.dag.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StartEndTimeTest {
    static StartEndTime startEndTime;

    @BeforeAll
    static void init() {
        startEndTime = new StartEndTime(0, 10, 5);
    }

    @Test
    void getStart() {
        assertEquals(0, startEndTime.getStart());
    }

    @Test
    void getEnd() {
        assertEquals(10, startEndTime.getEnd());
    }
    
    @Test
    void getNetworkingStart() {
    	assertEquals(5, startEndTime.getNetworkingStart());
    }

}
