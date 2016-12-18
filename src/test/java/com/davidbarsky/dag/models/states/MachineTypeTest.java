package com.davidbarsky.dag.models.states;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

class MachineTypeTest {
    @Test
    void getCostForSmallMachine() {
        MachineType smallMachine = MachineType.SMALL;
        assertEquals(new Integer(2), smallMachine.getCost());
    }

    @Test
    void getCostForLargeMachine() {
        MachineType largeMachine = MachineType.LARGE;
        assertEquals(new Integer(4), largeMachine.getCost());
    }
}
