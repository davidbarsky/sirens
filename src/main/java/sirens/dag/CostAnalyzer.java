package sirens.dag;

import java.util.List;

import sirens.models.TaskQueue;

public class CostAnalyzer {
    private CostAnalyzer() {}

    public static int findCostOfBuiltTasks(List<TaskQueue> tqs) {
        Boolean hasUnbuilt = tqs.stream().anyMatch(TaskQueue::hasUnbuiltTask);
        if (hasUnbuilt) {
            throw new DAGException("TaskQueues have unbuilt tasks!");
        }

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
