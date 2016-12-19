package info.rmarcus.dag.birkhoff;

import java.util.ArrayList;
import java.util.Collection;
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
		
		MetropolisHastingsPermutationSearch mhps = new MetropolisHastingsPermutationSearch(tasks.size(), (d) -> {
			int[] perm = CoeffAndMatrix.asFlatPerm(d);
			List<Task> t = new ArrayList<>(tasks.size());
			for (int i : perm) {
				t.add(tasks.get(i));
			}
			
			List<TaskQueue> tqs = PermutationSolver.greedySolve(t);
			return CostAnalyzer.getLatency(Actualizer.invoke(tqs));
		});
		
		for (int i = 0; i < 1000; i++)
			mhps.iterate();
	}
}
