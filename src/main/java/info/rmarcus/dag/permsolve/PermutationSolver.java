package info.rmarcus.dag.permsolve;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import com.davidbarsky.dag.Actualizer;
import com.davidbarsky.dag.CostAnalyzer;
import com.davidbarsky.dag.DAGGenerator;
import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.TaskQueue;
import com.davidbarsky.dag.models.states.MachineType;

import info.rmarcus.NullUtils;
import info.rmarcus.dag.permsolve.optimal.InOrderScheduleNode;
import info.rmarcus.dag.permsolve.optimal.PruningFlyweight;
import info.rmarcus.dag.permsolve.optimal.ScheduleNode;
import info.rmarcus.javautil.IteratorUtilities;
import info.rmarcus.javautil.StreamUtilities.Pair;


public class PermutationSolver {


	private static final int NUM_THREADS = 8;

	private int[][] graph;
	private int[] dfsStack;
	private int[] neighborCount;
	private int[] colors;
	
	public PermutationSolver(Collection<Task> tasks) {
		buildGraph(tasks);
	}
	
	private void buildGraph(Collection<Task> tasks) {
		graph = new int[tasks.size()][tasks.size()];
		
		for (int i = 0; i < graph.length; i++)
			Arrays.fill(graph[i], -1);
		
		for (Task parent : tasks) {
			int i = 0;
			for (Task child : parent.getDependents().keySet()) {
				graph[parent.getID()][i++] = child.getID();
			}
		}
		
		dfsStack = new int[tasks.size()];
		colors = new int[tasks.size()];
		neighborCount = new int[tasks.size()];
	}
	
	private boolean checkGraphForCycle() {
		Arrays.fill(dfsStack, -1);
		Arrays.fill(colors, 0);
		Arrays.fill(neighborCount, 0);
		
		int stackPtr = 0;
		
		// push the root onto the stack
		dfsStack[0] = 0;
		colors[0] = 1; // make it gray
		stackPtr++;
		
		while (stackPtr != 0) {
			int taskID = dfsStack[stackPtr - 1];
			
			// mark myself gray
			colors[taskID] = 1;
			
			int i = neighborCount[taskID];
			if (graph[taskID][i] != -1) {
				// add my next neighbor to the stack
				for (; graph[taskID][i] != -1; i++) {
					int child = graph[taskID][i];
					if (colors[child] == 1) // found another gray! it's a cycle
						return true;

					if (colors[child] == 0) {
						// add white nodes to the stack
						dfsStack[stackPtr++] = child;
						break;
					}

				}
				
				neighborCount[taskID] = (graph[taskID][i] == -1 ? i : i + 1);
				continue;
			}
			
			if (colors[taskID] == 1) {
				// I was already gray, so set my color to black and
				// stop (I'm being popped off the stack)
				colors[taskID] = 2; 
				dfsStack[--stackPtr] = -1;
				continue;
			}
			
		}
		
		return false;
		
	}


	public List<TaskQueue> forcedSolve(List<Task> tasks) {
		if (tasks.size() == 0)
			return new LinkedList<>();

		Deque<TaskQueue> toR = new LinkedList<>();

		TaskQueue first = new TaskQueue(MachineType.SMALL);
		toR.add(first);

		for (Task t : tasks) {
			TaskQueue proposed = toR.peek();
			// check to see if adding an edge from the previous item in this task queue
			// to this task would create a cycle
			
			// if it's empty, then we're fine.
			if (!proposed.getTasks().isEmpty()) {
				Task last = proposed.getTasks().get(proposed.getTasks().size()-1);
				
				int i = 0;
				boolean alreadyPresent = false;
				while (graph[last.getID()][i] != -1) {
					if (graph[last.getID()][i] == t.getID()) {
						alreadyPresent = true;
						break;
					}
						
					i++;
				}
				
				if (!alreadyPresent)
					graph[last.getID()][i] = t.getID();
				
				if (checkGraphForCycle()) {
					// this edge creates a cycle!
					// first, remove the edge.
					if (!alreadyPresent)
						graph[last.getID()][i] = -1;
					
					// add a new taskqueue
					proposed = new TaskQueue(MachineType.SMALL);
					toR.push(proposed);
				}
			}
			
			proposed.add(t);
		}

		return new ArrayList<>(toR);
	}

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

		dq.push(new InOrderScheduleNode(t, pf, costs));

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

	private static int evaluateRemovalOfPart(List<Task> t, Set<Integer> currentPartitions, int toRemove) {
		Set<Integer> newParts = new HashSet<Integer>(currentPartitions);
		newParts.remove(toRemove);
		List<TaskQueue> tqs = buildQueuesWithPartitions(t, newParts);
		final @Nullable List<TaskQueue> actualized = Actualizer.invoke(tqs);

		if (actualized == null)
			return -1;

		int costWithout = CostAnalyzer.getLatency(actualized);
		return costWithout;

	}

	private static int evaluateRemovalOfRange(List<Task> t, Set<Integer> current, Set<Integer> toConsider) {
		List<Task> localCopy = DAGGenerator.cloneTasks(t);

		int bestIdx = -1;
		int bestVal = Integer.MAX_VALUE;
		for (Integer i : toConsider) {
			int cost = evaluateRemovalOfPart(localCopy, current, i);

			if (cost == -1 || cost > bestVal) {
				continue;
			}

			// otherwise, we have a new best value!
			bestIdx = i;
			bestVal = cost;
		}

		return bestIdx;
	}

	private static int findBestPartitionGreedy(List<Task> t, Set<Integer> currentPartitions, ExecutorService exec) {
		List<Set<Integer>> chunks = new ArrayList<>();
		IntStream.range(0, NUM_THREADS).forEach(i -> chunks.add(new HashSet<>()));

		int cnt = 0;
		for (Integer i : currentPartitions) {
			NullUtils.orThrow(chunks.get(cnt++ % NUM_THREADS)).add(i);
		}

		Set<Future<Integer>> futures = chunks.stream()
				.map(s -> exec.submit(() -> evaluateRemovalOfRange(t, currentPartitions, s)))
				.collect(Collectors.toSet());

		return NullUtils.orThrow(
				futures.stream()
				.map(f -> NullUtils.wrapCall(f::get))
				.map(opt -> opt.get())
				.max((a, b) -> a - b)
				.orElse(-1));

	}

	private static List<TaskQueue> buildQueuesWithPartitions(List<Task> tasks, Set<Integer> partitions) {
		List<TaskQueue> tq = new ArrayList<>(partitions.size()+1);
		tq.add(new TaskQueue(MachineType.SMALL));
		int cnt = 0;
		for (Task t : tasks) {
			NullUtils.orThrow(tq.get(tq.size()-1)).add(t);
			if (partitions.contains(cnt))
				tq.add(new TaskQueue(MachineType.SMALL));
			cnt++;
		}
		return tq;
	}

	public static List<TaskQueue> greedySolve(List<Task> tasks) {
		Set<Integer> partitions = new HashSet<>();
		@NonNull ExecutorService es = NullUtils.orThrow(Executors.newFixedThreadPool(NUM_THREADS));

		for (int i = 0; i < tasks.size()-1; i++)
			partitions.add(i);

		int currentCost = CostAnalyzer.getLatency(Actualizer.invoke(buildQueuesWithPartitions(tasks, partitions)));
		while (true) {
			@Nullable final Integer bestIdx = findBestPartitionGreedy(tasks, partitions, es);
			if (bestIdx == -1)
				break;

			// see if removing this partition helps or hurts our cost
			Set<Integer> withoutPartition = new HashSet<>(partitions);
			withoutPartition.remove(bestIdx);

			List<TaskQueue> without = buildQueuesWithPartitions(tasks, withoutPartition);

			int withoutCost = CostAnalyzer.getLatency(Actualizer.invoke(without));

			if (withoutCost <= currentCost) {
				partitions.remove(bestIdx);
				currentCost = withoutCost;
			} else {
				break;
			}

		};

		es.shutdownNow();

		return buildQueuesWithPartitions(tasks, partitions);

	}

	public static List<TaskQueue> topoSolve(List<Task> tasks) {
		if (tasks.size() == 0)
			return new LinkedList<>();

		Deque<TaskQueue> toR = new LinkedList<>();

		TaskQueue first = new TaskQueue(MachineType.SMALL);
		first.add(tasks.get(0));
		toR.add(first);

		Iterator<Pair<Task, Task>> it = IteratorUtilities.twoGrams(tasks.iterator());
		while (it.hasNext()) {
			Pair<Task, Task> t = it.next();

			if (t.getB() == null) // ignore the end
				continue;

			if (t.getB().getID() < t.getA().getID()) {
				toR.add(new TaskQueue(MachineType.SMALL));
			}

			toR.peekLast().add(t.getB());	
		}

		return new ArrayList<>(toR);
	}



	public static void main(String[] args) {
		Task t1 = new Task(3, MachineType.latencyMap(2000));
		Task t2 = new Task(2, MachineType.latencyMap(2000));
		Task t3 = new Task(1, MachineType.latencyMap(20));
		Task t4 = new Task(0, MachineType.latencyMap(20));

		t1.addDependency(2, t3);
		t1.addDependency(4, t4);

		t2.addDependency(8, t3);
		t2.addDependency(16, t4);

		t3.addDependency(32, t4);



		final List<Task> tasks = Arrays.asList(t4, t3, t2, t1);

		List<TaskQueue> tqs = solve(tasks);
		System.out.println(tqs);

	}
}
