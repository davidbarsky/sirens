package edu.brandeis.dag.models.states;

import edu.brandeis.dag.DAGException;

// Associated values are costs of the machine
public enum MachineType {
    SMALL, LARGE;

    public Integer getCost() {
        switch (this) {
            case SMALL: return 2;
            case LARGE: return 4;
            default: throw new DAGException("Invariant broken; MachineType is not defined");
        }
    }
}
