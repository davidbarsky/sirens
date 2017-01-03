package info.rmarcus.dag.birkhoff;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.davidbarsky.dag.Actualizer;
import com.davidbarsky.dag.CostAnalyzer;
import com.davidbarsky.dag.DAGGenerator;
import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.TaskQueue;

import info.rmarcus.birkhoffvonneumann.CoeffAndMatrix;
import info.rmarcus.birkhoffvonneumann.learners.MetropolisHastingsPermutationSearch;
import info.rmarcus.dag.permsolve.PermutationSolver;
import info.rmarcus.dynamic_critical_path.DynamicCriticalPath;
import info.rmarcus.ggen4j.graph.Vertex;

public class BirkhoffScheduler {

	private List<Task> tasks;
	private PermutationSolver ps;
	private MetropolisHastingsPermutationSearch mhps;
	
	public void measure(int n) {
		Collection<Vertex> vertices = DAGGenerator.getSparseLU(n);
		tasks = DAGGenerator.verticesToTasks(vertices);
		
		ps = new PermutationSolver(tasks);
		
		System.out.println("Number of tasks: " + tasks.size());
		
//		Set<List<String>> clusters = DynamicCriticalPath.schedule(DAGGenerator.getVertexWeightMap(vertices), DAGGenerator.getEdgeWeightMap(vertices));
//		System.out.println(clusters);
//		Collection<TaskQueue> tqs = DAGGenerator.clustersToTasks(vertices, clusters);
//		tqs = Actualizer.invoke(tqs);
//		System.out.println("Cost: " + CostAnalyzer.getLatency((List<TaskQueue>) tqs));
		
		mhps = new MetropolisHastingsPermutationSearch(tasks.size(), this::loss);
		for (int i = 0; i < 200000; i++) {
			//System.out.println("Iteration " + i);
			mhps.iterate();
		}
		
		System.out.println(loss(mhps.getBest()));
	}
	
	public List<TaskQueue> getBest() {
		return Actualizer.invoke(permToTQs(mhps.getBest()));
	}
	
	private List<TaskQueue> permToTQs(double[][] d) {
		int[] perm = CoeffAndMatrix.asFlatPerm(d);
		List<Task> t = new ArrayList<>(tasks.size());
		for (int i : perm) {
			t.add(tasks.get(i));
		}
		
		List<TaskQueue> tqs = ps.forcedSolve(t);
		return tqs;
	}
	
	private double loss(double[][] d) {
		List<TaskQueue> tqs = permToTQs(d);
		int cost = CostAnalyzer.getLatency(Actualizer.invoke(tqs)); 
		return cost;
	}
	
	public static void main(String[] args) {
		BirkhoffScheduler bs = new BirkhoffScheduler();
		long t = System.currentTimeMillis();
		bs.measure(20);
		System.out.println(bs.getBest());
		//System.out.println(System.currentTimeMillis() - t);
	}
}
