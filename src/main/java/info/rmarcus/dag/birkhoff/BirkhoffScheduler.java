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

	private List<Task> tasks;
	
	public void measure(int n) {
		tasks = DAGGenerator.verticesToTasks(DAGGenerator.getPoisson(n));
		
		System.out.println("Number of tasks: " + tasks.size());
		
		MetropolisHastingsPermutationSearch mhps = new MetropolisHastingsPermutationSearch(tasks.size(), this::loss);

		
		for (int i = 0; i < 10000; i++) {
			//System.out.println("Iteration " + i);
			mhps.iterate();
		}
		
		System.out.println(loss(mhps.getBest()));
	}
	
	private double loss(double[][] d) {
		int[] perm = CoeffAndMatrix.asFlatPerm(d);
		List<Task> t = new ArrayList<>(tasks.size());
		for (int i : perm) {
			t.add(tasks.get(i));
		}
		
		List<TaskQueue> tqs = PermutationSolver.topoSolve(t);
		int cost = CostAnalyzer.getLatency(Actualizer.invoke(tqs)); 
		return cost;
	}
	
	public static void main(String[] args) {
		BirkhoffScheduler bs = new BirkhoffScheduler();
		bs.measure(20);
	}
}
