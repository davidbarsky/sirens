package edu.brandeis.dag.permsolve;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.Nullable;

import edu.brandeis.dag.Actualizer;
import edu.brandeis.dag.models.Task;
import edu.brandeis.dag.models.TaskQueue;
import edu.brandeis.dag.models.states.MachineType;
import info.rmarcus.NullUtils;

public class ScheduleNode {
	private final List<Task> tasks;
	private final StarsAndBarsNode sbn;
	private final @Nullable ScheduleNode parent;
	private final Map<Integer, ScheduleNode> children;

	private int lowerBound;
	private int currentBest;
	private int myCost;

	public ScheduleNode(List<Task> t) {
		this.tasks = t;
		sbn = new StarsAndBarsNode(t.size());
		parent = null;
		children = new HashMap<>();
		lowerBound = 0;
		calculateMyCost();
	}

	private ScheduleNode(ScheduleNode parent, StarsAndBarsNode sbn) {
		this.tasks = parent.tasks;
		this.sbn = sbn;
		this.parent = parent;
		children = new HashMap<>();
		calculateLowerBound();
		calculateMyCost();
	}

	private void calculateLowerBound() {
		// first, calculate the lower bound on me and my children.
		// my lower bound is the sum of the edges that have currently been broken
		lowerBound = sbn.getAllDisconnectedPairs().stream()
				.mapToInt(p -> intToTask(p.a).getCostTo(intToTask(p.b)))
				.sum();
	}
	
	private void calculateMyCost() {
		myCost = Actualizer.getCost(getTaskQueues());
	}

	public List<TaskQueue> getTaskQueues() {
		return NullUtils.orThrow(sbn.getPartitions().stream()
				.map(l -> l.stream().map(this::intToTask).collect(Collectors.toList()))
				.map(l -> new TaskQueue(MachineType.LARGE, l))
				.collect(Collectors.toList()),
				() -> new PermSolveException("Could not build task queues!"));

	}

	public int getNumChildren() {
		return sbn.getNumChildren();
	}

	public ScheduleNode getChild(int n) {
		if (children.containsKey(n))
			return NullUtils.orThrow(children.get(n),
					() -> new PermSolveException("Child of scheduler node disappeared!"));

		ScheduleNode toR = new ScheduleNode(this, sbn.getChild(n));
		children.put(n, toR);
		return toR;
	}

	private Task intToTask(int i) {
		return NullUtils.orThrow(tasks.get(i),
				() -> new PermSolveException("Null in task list!"));
	}
}
