package info.rmarcus.dag.permsolve;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.TaskQueue;
import com.davidbarsky.dag.models.states.MachineType;

import info.rmarcus.NullUtils;

public class PermutationSolver {
	
	
	
//	public List<TaskQueue> solve() {
//		
//		return null;
//	}
	
//	private static void printTree(StarsAndBarsNode p, int level) {		
//		for (int i = 0; i < level; i++)
//			System.out.print("  ");
//		
//		System.out.println(p + " " + p.getPartitions());
//		
//		for (int i = 0; i < p.getNumChildren(); i++) {
//			printTree(p.getChild(i), level + 1);
//		}
//	}
	
	
	public static List<TaskQueue> solve(List<Task> t) {
		Deque<ScheduleNode> dq = new LinkedList<>();
		PruningFlyweight pf = new PruningFlyweight();
		dq.push(new ScheduleNode(t, pf));
		
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
	
	public static void main(String[] args) {
		Task t1 = new Task(1, MachineType.latencyMap(20));
		Task t2 = new Task(2, MachineType.latencyMap(20));
		Task t3 = new Task(3, MachineType.latencyMap(20));
		Task t4 = new Task(4, MachineType.latencyMap(20));
		
		t4.addDependency(1000, t3);
		t4.addDependency(1, t2);
		t3.addDependency(1, t1);
		t2.addDependency(1, t1);
		
		final List<Task> tasks = Arrays.asList(t2, t1, t3, t4);
		
		if (tasks != null)
			System.out.println(solve(tasks));
		
	}
}
