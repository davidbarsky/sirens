package com.davidbarsky.dag.models;



import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

class StartEndTimeTest {
    static StartEndTime startEndTime;

    @Before
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
