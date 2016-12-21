package com.davidbarsky.dag;

import java.util.List;

import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.TaskQueue;
import com.davidbarsky.dag.models.states.MachineType;

public class CostAnalyzer {
    private CostAnalyzer() {}

    public static Integer findCost(List<Task> tasks, MachineType machineType) {
        Task first = tasks.get(0);
        Task last = tasks.get(tasks.size() - 1);

        return (last.getStartEndTime().get().getEnd() -
                first.getStartEndTime().get().getStart()) * machineType.getCost();
    }
    
    public static int findCost(List<TaskQueue> tqs) {
    	return tqs.stream()
    			.mapToInt(tq -> (tq.getEndTime() - tq.getStartTime()) * tq.getMachineType().getCost())
    			.sum();
    }
    
    public static int getLatency(List<TaskQueue> tqs) {
    	if (tqs == null)
    		return Integer.MAX_VALUE;
    	
    	return tqs.stream()
    			.mapToInt(tq -> tq.getEndTime() + 60)
    			.max().getAsInt();
    }
}
