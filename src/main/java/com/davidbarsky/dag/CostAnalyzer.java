package com.davidbarsky.dag;

import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.states.MachineType;

import java.util.List;

public class CostAnalyzer {
    private CostAnalyzer() {}

    public static Integer findCost(List<Task> tasks, MachineType machineType) {
        Task first = tasks.get(0);
        Task last = tasks.get(tasks.size() - 1);

        return (last.getStartEndTime().get().getEnd() -
                first.getStartEndTime().get().getStart()) * machineType.getCost();
    }
}
