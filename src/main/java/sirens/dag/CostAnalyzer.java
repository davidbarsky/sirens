package sirens.dag;

import java.util.List;

import sirens.dag.models.Task;
import sirens.dag.models.TaskQueue;
import sirens.dag.models.states.MachineType;

public class CostAnalyzer {
    private CostAnalyzer() {}

    public static int findCostOfBuiltTasks(List<Task> tasks, MachineType machineType) {
        if (tasks.stream().anyMatch(task -> !task.isBuilt())) {
            throw new RuntimeException("TaskQueues have unbuilt tasks!");
        }

        Task first = tasks.get(0);
        Task last = tasks.get(tasks.size() - 1);

        return (last.getStartEndTime().get().getEnd() -
                first.getStartEndTime().get().getStart()) * machineType.getCost();
    }
    
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
