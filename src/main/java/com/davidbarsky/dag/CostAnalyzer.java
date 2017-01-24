package com.davidbarsky.dag;

import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.TaskQueue;
import com.davidbarsky.dag.models.states.MachineType;

import java.util.Collection;
import java.util.List;

public class CostAnalyzer {
    private CostAnalyzer() {}

    public static Integer findCost(List<Task> tasks, MachineType machineType) {
        Task first = tasks.get(0);
        Task last = tasks.get(tasks.size() - 1);

        return (last.getStartEndTime().get().getEnd() -
                first.getStartEndTime().get().getStart()) * machineType.getCost();
    }
    
    public static int findCost(Collection<TaskQueue> tqs) {
        if (tqs.stream().anyMatch(tq -> tq.hasUnbuiltTask()))
            throw new DAGException("You must actualize this schedule before costing it!");

    	return tqs.stream()
    			.mapToInt(tq -> (tq.getEndTime() - tq.getStartTime()) * tq.getMachineType().getCost())
    			.sum();
    }
    
    public static int getLatency(Collection<TaskQueue> tqs) {
        if (tqs.stream().anyMatch(tq -> tq.hasUnbuiltTask()))
            throw new DAGException("You must actualize this schedule before costing it!");

    	return tqs.stream()
    			.mapToInt(tq -> tq.getEndTime() + 60)
    			.max().getAsInt();
    }
}
