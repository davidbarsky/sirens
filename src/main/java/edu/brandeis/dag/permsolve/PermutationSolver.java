package edu.brandeis.dag.permsolve;

import java.util.List;

import edu.brandeis.dag.models.Task;

public class PermutationSolver {
	
	private final List<Task> tasks;
	
	public PermutationSolver(List<Task> tasks) {
		this.tasks = tasks;
	}
	
//	public List<TaskQueue> solve() {
//		
//		return null;
//	}
	
	private static void printTree(StarsAndBarsNode p, int level) {
//		if (level == 3)
//			return;
		
		for (int i = 0; i < level; i++)
			System.out.print("  ");
		
		System.out.println(p + " " + p.getAllDisconnectedPairs());
		
		for (int i = 0; i < p.getNumChildren(); i++) {
			printTree(p.getChild(i), level + 1);
		}
	}
	
	public static void main(String[] args) {
		StarsAndBarsNode p = new StarsAndBarsNode(4);
		
		printTree(p, 0);
	}
}
