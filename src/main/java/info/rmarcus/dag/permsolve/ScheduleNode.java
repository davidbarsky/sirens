package info.rmarcus.dag.permsolve;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.davidbarsky.dag.Actualizer;
import com.davidbarsky.dag.CostAnalyzer;
import com.davidbarsky.dag.DAGException;
import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.TaskQueue;
import com.davidbarsky.dag.models.states.MachineType;

import info.rmarcus.NullUtils;

public class ScheduleNode {
	private final List<Task> tasks;
	private final StarsAndBarsNode sbn;
	private final Map<Integer, ScheduleNode> children;

	private int lowerBound;
	private int myCost;

	private PruningFlyweight prune;

	public ScheduleNode(List<Task> t, PruningFlyweight pf) {
		this.tasks = t;
		sbn = new StarsAndBarsNode(t.size());
		children = new HashMap<>();
		lowerBound = 0;
		prune = pf;
		calculateMyCost();
	}

	private ScheduleNode(ScheduleNode parent, StarsAndBarsNode sbn, PruningFlyweight pf) {
		this.tasks = parent.tasks;
		this.sbn = sbn;
		children = new HashMap<>();
		this.prune = pf;
		calculateLowerBound();
		calculateMyCost();
	}

	private void calculateLowerBound() {
		// my lower bound is the sum of the edges that have currently been broken
		lowerBound = sbn.getAllDisconnectedPairs().stream()
				.mapToInt(p -> intToTask(p.a).getCostTo(intToTask(p.b)))
				.sum();
	}

	private void calculateMyCost() {
		//myCost = CostAnalyzer.findCost(getTaskQueues());
		try {
			myCost = CostAnalyzer.getLatency(getTaskQueues());
		} catch (DAGException e) {
			//System.out.println("Could not schedule partition: " + sbn.toString());
			myCost = Integer.MAX_VALUE;
		}
		prune.recordNewObserved(myCost, this);
	}

	public List<TaskQueue> getSchedule() {
		return Actualizer.invoke(getTaskQueues());
	}

	public List<TaskQueue> getTaskQueues() {

		List<TaskQueue> toR = new LinkedList<>();
		for (List<Integer> partition : sbn.getPartitions()) {
			TaskQueue tq = new TaskQueue(MachineType.SMALL);
			for (Integer taskID : partition) {
				tq.add(intToTask(taskID));
			}
			toR.add(tq);
		}

		return Actualizer.invoke(toR);
	}

	public int getNumChildren() {
		if (prune.isAboveBestObserved(lowerBound))
			return 0; // I've been pruned!

		return sbn.getNumChildren();
	}

	public ScheduleNode getChild(int n) {
		if (children.containsKey(n))
			return NullUtils.orThrow(children.get(n),
					() -> new PermSolveException("Child of scheduler node disappeared!"));

		ScheduleNode toR = new ScheduleNode(this, sbn.getChild(n), prune);
		children.put(n, toR);
		return toR;
	}

	public boolean isPruned() {
		return prune.isAboveBestObserved(lowerBound);
	}

	private Task intToTask(int i) {
		return NullUtils.orThrow(tasks.get(i),
				() -> new PermSolveException("Null in task list!"));
	}

	@Override
	public String toString() {
		return "cost: " + myCost + " lb: " + lowerBound + " " + sbn.toString() + " (fw: " + prune.getBestObservedCost() + ")";
	}


}
