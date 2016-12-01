package com.davidbarsky.dag.models.states;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
