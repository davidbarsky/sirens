package edu.brandeis.dag;

import edu.brandeis.dag.models.Task;
import edu.brandeis.dag.models.states.MachineType;

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
