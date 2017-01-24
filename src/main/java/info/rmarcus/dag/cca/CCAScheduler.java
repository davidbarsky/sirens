package info.rmarcus.dag.cca;

import com.davidbarsky.dag.Actualizer;
import com.davidbarsky.dag.CostAnalyzer;
import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.TaskQueue;
import com.davidbarsky.dag.models.states.MachineType;

import java.util.*;
import java.util.stream.Collectors;

public class CCAScheduler {
	public static Collection<TaskQueue> cca(Collection<Task> tasks, int deadline) {
		// first, cluster each task into it's own task queue (the initial clustering)
		List<TaskQueue> toR = new ArrayList<>(tasks.size());
		for (Task t : tasks) {
			TaskQueue toAdd = new TaskQueue(MachineType.SMALL);
			toAdd.add(t);
			toR.add(toAdd);
		}

		// next, compute the priority of each cluster, which is the max
		// priority of a task within a cluster
		Map<TaskQueue, Integer> priority = new HashMap<>();

		for (TaskQueue tq : toR) {
			priority.put(tq, computePriority(tq.getTasks(), deadline)
					.values().stream()
					.mapToInt(i -> i)
					.max().orElse(0));
;		}

		// for each cluster, find all the clusters with a lower priority and consider a merge.
		for (TaskQueue tq : toR) {
			Set<TaskQueue> candidates = toR.stream()
					.filter(c -> c != tq)
					.filter(c -> priority.get(c) <= priority.get(tq))
					.collect(Collectors.toSet());

		}

		return null;
	}

	private static Map<Task, Integer> computePriority(Collection<Task> tasks, int deadline) {
		Map<Task, Integer> toR = new HashMap<>();

		Map<Task, Integer> est = computeEST(tasks);
		Map<Task, Integer> lft = computeLFT(tasks, deadline);

		for (Task t : tasks) {
			toR.put(t, lft.get(t) - est.get(t));
		}

		return toR;
	}

	private static int score(Collection<TaskQueue> tqs) {
		for (TaskQueue tq : tqs) {
			tq.unbuildAll();
			tq.sortTasksByID();
		}

		return CostAnalyzer.findCostOfBuiltTasks(Actualizer.actualize(tqs));
	}

	private static Map<Task, Integer> computeEST(Collection<Task> tasks) {
		Map<Task, Integer> est = new HashMap<>();
		Deque<Task> queue = new LinkedList<>();
		Set<Task> enqueued = new HashSet<>();
		queue.addAll(getSources(tasks));

		// the EST of each root is 0
		for (Task t : queue) {
			est.put(t, 0);
			enqueued.add(t);
		}
		
		// for each element in the queue, set the earliest start time of each task to
		// the max of the sum of each parents EST + their latency + their edge to me
		while (!queue.isEmpty()) {
			Task next = queue.pop();
			int v = next.getDependencies().keySet()
					.stream()
					.mapToInt(t -> communication(t, next) + latency(t) + est.get(t))
					.max().orElse(0);

			est.put(next, v);

			// enqueue everything we don't already have
			next.getDependents().keySet()
					.stream()
					.filter(t -> !enqueued.contains(t))
					.forEach(t -> {
						enqueued.add(t);
						queue.add(t);
					});
		}

		return est;
	}

	private static Map<Task, Integer> computeLFT(Collection<Task> tasks, int deadline) {
		Map<Task, Integer> lft = new HashMap<>();
		Deque<Task> queue = new LinkedList<>();
		Set<Task> enqueued = new HashSet<>();

		queue.addAll(getSinks(tasks));

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

	private static int latency(Task t) {
		return t.getLatencies().get(t.getTaskQueue().getMachineType());
	}

	private static int communication(Task t1, Task t2) {
		if (t1.getTaskQueue() == t2.getTaskQueue())
			return 0;

		if (!t1.getDependents().containsKey(t2))
			return 0;

		return t1.getDependents().get(t2);
	}

	private static Set<Task> getSources(Collection<Task> tasks) {
		return tasks.stream()
				.filter(t -> t.getDependencies().size() == 0)
				.collect(Collectors.toSet());
	}

	private static Set<Task> getSinks(Collection<Task> tasks) {
		return tasks.stream()
				.filter(t -> t.getDependents().size() == 0)
				.collect(Collectors.toSet());
	}
}
