package info.rmarcus.dag.permsolve;

import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

import com.davidbarsky.dag.Actualizer;
import com.davidbarsky.dag.CostAnalyzer;
import com.davidbarsky.dag.DAGException;
import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.TaskQueue;
import com.davidbarsky.dag.models.states.MachineType;

import info.rmarcus.NullUtils;

public class PermutationSolver {





	private static int[][] getPairwiseCost(List<Task> tasks) {
		int[][] toR = new int[tasks.size()][tasks.size()];

		for (int i = 0; i < tasks.size(); i++) {
			for (int j = i+1; j < tasks.size(); j++) {
				Task a = NullUtils.orThrow(tasks.get(i));
				Task b = NullUtils.orThrow(tasks.get(j));
				int cost = a.getCostTo(b);

				toR[i][j] = cost;
				toR[j][i] = cost;
			}
		}

		return toR;
	}

	public static List<TaskQueue> solve(List<Task> t) {
		Deque<ScheduleNode> dq = new LinkedList<>();
		PruningFlyweight pf = new PruningFlyweight();
		int[][] costs = getPairwiseCost(t);

		dq.push(new ScheduleNode(t, pf, costs));

		while (!dq.isEmpty()) {
			ScheduleNode sn = NullUtils.orThrow(dq.remove());
			System.out.println("Expanding: " + sn + " children: " + sn.getNumChildren());
			for (int i = 0; i < sn.getNumChildren(); i++) {
				ScheduleNode child = sn.getChild(i);

				if (child.isPruned()) {
					//System.out.println("\tpruned: " + child);
					continue;
				}

				//System.out.println("\t" + child);
				dq.add(child);
			}
		}

		return pf.getBestPartitions().getSchedule();

	}

	private static @Nullable Integer findBestPartitionGreedy(List<Task> t, Set<Integer> currentPartitions) {
		int bestCost = Integer.MAX_VALUE;
		Integer bestIdx = null;

		for (Integer i : currentPartitions) {			
			try {
				Set<Integer> newParts = new HashSet<Integer>(currentPartitions);
				newParts.remove(i);
				int costWithout = CostAnalyzer.getLatency(Actualizer.invoke(buildQueuesWithPartitions(t, newParts)));

				if (costWithout < bestCost) {
					bestCost = costWithout;
					bestIdx = i;
				}
			} catch (DAGException e) {
				// skip this one, can't remove it. 
			}
		}

		return bestIdx;

	}

	private static List<TaskQueue> buildQueuesWithPartitions(List<Task> tasks, Set<Integer> partitions) {
		LinkedList<TaskQueue> tq = new LinkedList<>();
		tq.push(new TaskQueue(MachineType.SMALL));
		int cnt = 0;
		for (Task t : tasks) {
			NullUtils.orThrow(tq.peek()).add(t);
			if (partitions.contains(cnt))
				tq.push(new TaskQueue(MachineType.SMALL));
			cnt++;
		}
		return tq;
	}

	public static List<TaskQueue> greedySolve(List<Task> tasks) {
		Set<Integer> partitions = new HashSet<>();

		for (int i = 0; i < tasks.size()-1; i++)
			partitions.add(i);

		int currentCost = CostAnalyzer.getLatency(Actualizer.invoke(buildQueuesWithPartitions(tasks, partitions)));
		while (true) {
			@Nullable final Integer bestIdx = findBestPartitionGreedy(tasks, partitions);
			if (bestIdx == null)
				break;

			// see if removing this partition helps or hurts our cost
			Set<Integer> withPartition = new HashSet<>(partitions);
			withPartition.remove(bestIdx);

			List<TaskQueue> with = buildQueuesWithPartitions(tasks, withPartition);

			int without = CostAnalyzer.getLatency(Actualizer.invoke(with));

			if (without < currentCost) {
				partitions.remove(bestIdx);
			} else {
				break;
			}

		};

		return buildQueuesWithPartitions(tasks, partitions);

	}


	public static void main(String[] args) {
		Task t1 = new Task(1, MachineType.latencyMap(2000));
		Task t2 = new Task(2, MachineType.latencyMap(2000));
		Task t3 = new Task(3, MachineType.latencyMap(20));
		Task t4 = new Task(4, MachineType.latencyMap(20));

		t1.addDependency(2, t3);
		t1.addDependency(4, t4);

		t2.addDependency(8, t3);
		t2.addDependency(16, t4);

		t3.addDependency(32, t4);



		final List<Task> tasks = Arrays.asList(t4, t3, t2, t1);

		if (tasks != null) {
			List<TaskQueue> tqs = greedySolve(tasks);
			System.out.println(tqs);
		}

	}
}
