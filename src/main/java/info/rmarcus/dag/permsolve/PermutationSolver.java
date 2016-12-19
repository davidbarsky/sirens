package info.rmarcus.dag.permsolve;

import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import com.davidbarsky.dag.Actualizer;
import com.davidbarsky.dag.CostAnalyzer;
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

	private static int findBestPartitionEdgeGreedy(List<Task> t) {
		// find the point in the list where inserting a partition will
		// break the lowest edge weights possible, using the 
		// sums of the columns and rows in the triangular matrix
		
		int[][] costs = getPairwiseCost(t);
		int[] triangCols = new int[t.size()];
		int[] triangRows = new int[t.size()];

		for (int i = 0; i < t.size(); i++)
			for (int j = 0; j < i; j++)
				triangRows[i] += costs[i][j];
			
		for (int i = 0; i < t.size(); i++)
			for (int j = i; j < t.size(); j++)
				triangCols[i] += costs[i][j];

		int bestIdx = 0;
		int bestSoFar = Integer.MAX_VALUE;

		int curVal = triangCols[0];
		for (int i = 1; i < t.size(); i++) {
			if (bestSoFar > curVal) {
				bestSoFar = curVal;
				bestIdx = i - 1;
			}
			curVal -= triangRows[i];
			curVal += triangCols[i];
		}

		return bestIdx;
	}

	private static int findBestPartitionGreedy(List<Task> t, Set<Integer> currentPartitions, int start, int stop) {
		
		int currBestIdx = start;
		int currBestVal = Integer.MAX_VALUE;
		for (int i = start; i < stop; i++) {
			Set<Integer> newParts = new HashSet<>(currentPartitions);
			newParts.add(i);
			
			
			int cost = Integer.MAX_VALUE;
			try {
				cost = CostAnalyzer.getLatency(Actualizer.invoke(buildQueuesWithPartitions(t, newParts)));
			} catch (Exception e) {
				cost = Integer.MAX_VALUE;
			}
			
			if (cost < currBestVal) {
				currBestVal = cost;
				currBestIdx = i;
			}
		}
		
		return currBestIdx;
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
		
	public static List<TaskQueue> edgeGreedySolve(List<Task> tasks) {
		Deque<Pair> toPartition = new LinkedList<>();
		Set<Integer> partitions = new HashSet<>();
		
		toPartition.push(new Pair(0, tasks.size()-1));
		
		while (!toPartition.isEmpty()) {
			Pair next = NullUtils.orThrow(toPartition.pop());
			int bestIdx = findBestPartitionEdgeGreedy(NullUtils.orThrow(tasks.subList(next.a, next.b)));
			
			// see if adding this partition helps or hurts our cost
			
			List<TaskQueue> without = buildQueuesWithPartitions(tasks, partitions);
			int withoutCost = CostAnalyzer.getLatency(Actualizer.invoke(without));
		
			Set<Integer> withPartition = new HashSet<>(partitions);
			withPartition.add(bestIdx);
			List<TaskQueue> with = buildQueuesWithPartitions(tasks, withPartition);
			int withCost = CostAnalyzer.getLatency(Actualizer.invoke(with));
			
			if (withCost < withoutCost) {
				partitions.add(bestIdx);
				toPartition.push(new Pair(next.a, bestIdx));
				toPartition.push(new Pair(bestIdx, next.b));
			}
		}
		
		return buildQueuesWithPartitions(tasks, partitions);
		
	}
	
	public static List<TaskQueue> greedySolve(List<Task> tasks) {
		Deque<Pair> toPartition = new LinkedList<>();
		Set<Integer> partitions = new HashSet<>();
		
		toPartition.push(new Pair(0, tasks.size()-1));
		
		while (!toPartition.isEmpty()) {
			Pair next = NullUtils.orThrow(toPartition.pop());
			int bestIdx = findBestPartitionGreedy(tasks, partitions, next.a, next.b);
			
			// see if adding this partition helps or hurts our cost
			
			List<TaskQueue> without = buildQueuesWithPartitions(tasks, partitions);
			int withoutCost = CostAnalyzer.getLatency(Actualizer.invoke(without));
		
			Set<Integer> withPartition = new HashSet<>(partitions);
			withPartition.add(bestIdx);
			List<TaskQueue> with = buildQueuesWithPartitions(tasks, withPartition);
			int withCost = CostAnalyzer.getLatency(Actualizer.invoke(with));
		
			if (withCost < withoutCost) {
				partitions.add(bestIdx);
				toPartition.push(new Pair(next.a, bestIdx));
				toPartition.push(new Pair(bestIdx, next.b));
			}
		}
		
		return buildQueuesWithPartitions(tasks, partitions);
		
	}
	
	private static class Pair {
		final int a, b;
		Pair(int a, int b) {
			this.a = a;
			this.b = b;
		}
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
