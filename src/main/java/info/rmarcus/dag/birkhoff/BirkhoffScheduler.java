package info.rmarcus.dag.birkhoff;

import java.util.ArrayList;
import java.util.List;

import com.davidbarsky.dag.Actualizer;
import com.davidbarsky.dag.CostAnalyzer;
import com.davidbarsky.dag.DAGGenerator;
import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.TaskQueue;

import info.rmarcus.birkhoffvonneumann.CoeffAndMatrix;
import info.rmarcus.birkhoffvonneumann.learners.MetropolisHastingsPermutationSearch;
import info.rmarcus.dag.permsolve.PermutationSolver;

public class BirkhoffScheduler {

	public static void main(String[] args) {
		List<Task> tasks = DAGGenerator.verticesToTasks(DAGGenerator.getCholesky());
		
		System.out.println("Number of tasks: " + tasks.size());
		
		MetropolisHastingsPermutationSearch mhps = new MetropolisHastingsPermutationSearch(tasks.size(), (d) -> {
			int[] perm = CoeffAndMatrix.asFlatPerm(d);
			List<Task> t = new ArrayList<>(tasks.size());
			for (int i : perm) {
				t.add(tasks.get(i));
			}
			
			List<TaskQueue> tqs = PermutationSolver.greedySolve(t);
			int cost = CostAnalyzer.getLatency(Actualizer.invoke(tqs)); 
			return cost;
		});
		
		for (int i = 0; i < 50; i++) {
			System.out.println("Iteration " + i);
			mhps.iterate();
		}
	}
}
