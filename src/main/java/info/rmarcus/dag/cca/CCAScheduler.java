package info.rmarcus.dag.cca;

import com.davidbarsky.dag.Actualizer;
import com.davidbarsky.dag.CostAnalyzer;
import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.TaskQueue;
import com.davidbarsky.dag.models.states.MachineType;

import java.util.*;
import java.util.stream.Collectors;

public class CCAScheduler {

	private List<Task> topo;

	public CCAScheduler(Collection<Task> tasks) {
		this.topo = tasks.stream()
				.sorted((a, b) -> a.getID() - b.getID())
				.collect(Collectors.toList());
	}

	public Collection<TaskQueue> schedule(int deadline) {
		// first, cluster each task into it's own task queue (the initial clustering)
		List<TaskQueue> toR = new ArrayList<>(topo.size());
		for (Task t : topo) {
			TaskQueue toAdd = new TaskQueue(MachineType.SMALL);
			toAdd.add(t);
			toR.add(toAdd);
		}

		while (attemptMerge(toR, deadline));

		return toR;
	}

	private boolean attemptMerge(List<TaskQueue> toR, int deadline) {
		// compute the priority of each cluster, which is the max
		// priority of a task within a cluster
		Map<TaskQueue, Integer> priority = new HashMap<>();

		for (TaskQueue tq : toR) {
			priority.put(tq, computePriority(tq.getTasks(), deadline)
					.values().stream()
					.mapToInt(i -> i)
					.max().orElse(0));
		}

		// for each cluster, find all the clusters with a lower priority and consider a merge.
		for (TaskQueue tq : toR) {
			TaskQueue[] candidates = toR.stream()
					.filter(c -> c != tq)
					.filter(c -> priority.get(c) <= priority.get(tq))
					.toArray(i -> new TaskQueue[i]);

			// check every pair c1, c2 in candidates for a merge. For each pair, we need to consider:
			// 1. the pair alone
			// 2. the pair on a small machine
			// 3. the pair on a large machine
			int aloneCost = score(toR);
			for (int i = 0; i < candidates.length; i++) {
				TaskQueue c1 = candidates[i];
				for (int j = i; j < candidates.length; j++) {
					TaskQueue c2 = candidates[j];

					// first, try merging the clusters into a small machine
					int smallMergedCost = scoreCombination(toR, c1, c2, MachineType.SMALL);

					// second, try merging the clusters into a large machine
					int largeMergedCost = scoreCombination (toR, c1, c2, MachineType.LARGE);

					if (smallMergedCost <= largeMergedCost && smallMergedCost < aloneCost) {
						// accept the merge onto the small machine
						toR.remove(c1);
						toR.remove(c2);

						TaskQueue toAdd = new TaskQueue(MachineType.SMALL);
						c1.getTasks().forEach(toAdd::add);
						c2.getTasks().forEach(toAdd::add);
						toR.add(toAdd);
						return true;
					}

					if (largeMergedCost < smallMergedCost && largeMergedCost < aloneCost) {
						// accept the merge onto the small machine
						toR.remove(c1);
						toR.remove(c2);

						TaskQueue toAdd = new TaskQueue(MachineType.LARGE);
						c1.getTasks().forEach(toAdd::add);
						c2.getTasks().forEach(toAdd::add);
						toR.add(toAdd);
						return true;	
					}
				}
			}

		}
		// couldn't find any good merges.
		return false;

	}

	private Map<Task, Integer> computePriority(Collection<Task> tasks, int deadline) {
		Map<Task, Integer> toR = new HashMap<>();

		Map<Task, Integer> est = computeEST();
		Map<Task, Integer> lft = computeLFT(deadline);

		for (Task t : tasks) {
			toR.put(t, lft.get(t) - est.get(t));
		}

		return toR;
	}

	private int scoreCombination(Collection<TaskQueue> tqs, TaskQueue c1, TaskQueue c2, MachineType mt) {
		Set<TaskQueue> withMerged = new HashSet<>(tqs);
		withMerged.remove(c1);
		withMerged.remove(c2);
		TaskQueue merged = new TaskQueue(mt);
		c1.getTasks().stream().forEach(merged::add);
		c2.getTasks().stream().forEach(merged::add);
		withMerged.add(merged);
		return score(withMerged);
	}

	private int score(Collection<TaskQueue> tqs) {
		for (TaskQueue tq : tqs) {
			tq.unbuildAll();
			tq.sortTasksByID();
		}

		return CostAnalyzer.findCost(Actualizer.actualize(tqs));
	}

	private Map<Task, Integer> computeEST() {
		Map<Task, Integer> est = new HashMap<>();

		// for each task in the list, set the earliest start time of each task to
		// the max of the sum of each parents EST + their latency + their edge to me
		for (Task next : topo) {
			int v = next.getDependencies().keySet()
					.stream()
					.mapToInt(t -> communication(t, next) + latency(t) + est.get(t))
					.max().orElse(0);

			est.put(next, v);
		}

		return est;
	}

	private Map<Task, Integer> computeLFT(int deadline) {
		Map<Task, Integer> lft = new HashMap<>();
		Deque<Task> queue = new LinkedList<>();
		Set<Task> enqueued = new HashSet<>();

		queue.addAll(getSinks(topo));

		// the LFT of each sink is the deadline
		for (Task sink : queue) {
			lft.put(sink, deadline);
			enqueued.add(sink);
		}

		// moving upwards from the bottom of the graph, set every node's LFT
		// to the minimum of their children's: lft - latency - communication
		while (!queue.isEmpty()) {
			Task next = queue.pop();
			int v = next.getDependents().keySet()
					.stream()
					.mapToInt(t -> lft.get(t) + latency(t) + communication(next, t))
					.min().orElse(deadline);

			
			lft.put(next, v);
			// enqueue all of our parents that we don't already have
			next.getDependencies().keySet()
			.stream()
			.filter(t -> !enqueued.contains(t))
			.forEach(t -> {
				enqueued.add(t);
				queue.add(t);
			});
		}

		return lft;
	}

	private int latency(Task t) {
		return t.getLatencies().get(t.getTaskQueue().getMachineType());
	}

	private int communication(Task t1, Task t2) {
		if (t1.getTaskQueue() == t2.getTaskQueue())
			return 0;

		if (!t1.getDependents().containsKey(t2))
			return 0;

		return t1.getDependents().get(t2);
	}

	private Set<Task> getSources(Collection<Task> tasks) {
		return tasks.stream()
				.filter(t -> t.getDependencies().size() == 0)
				.collect(Collectors.toSet());
	}

	private Set<Task> getSinks(Collection<Task> tasks) {
		return tasks.stream()
				.filter(t -> t.getDependents().size() == 0)
				.collect(Collectors.toSet());
	}
}
