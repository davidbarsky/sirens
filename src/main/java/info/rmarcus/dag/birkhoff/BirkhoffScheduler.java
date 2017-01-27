package info.rmarcus.dag.birkhoff;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToDoubleFunction;

import com.davidbarsky.dag.CostAnalyzer;
import com.davidbarsky.dag.DAGGenerator;
import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.TaskQueue;

import info.rmarcus.birkhoffvonneumann.CoeffAndMatrix;
import info.rmarcus.birkhoffvonneumann.MatrixUtils;
import info.rmarcus.birkhoffvonneumann.exceptions.BVNException;
import info.rmarcus.birkhoffvonneumann.learners.generalized_loss.MHJointPermutationLearner;
import info.rmarcus.dag.birkhoff.permsolve.PermutationSolver;
import info.rmarcus.dag.cca.CCAScheduler;
import info.rmarcus.dag.sla.SLA;
import info.rmarcus.dag.sla.TotalLatencySLA;
import info.rmarcus.ggen4j.graph.Vertex;

public class BirkhoffScheduler {

	private List<Task> tasks;
	private MHJointPermutationLearner jps;

	private final static int DEADLINE = 2000;


	public void measure(int n) {
		List<Vertex> vertices = new ArrayList<>(DAGGenerator.getSparseLU(n));
		tasks = DAGGenerator.verticesToTasks(vertices);
				
		System.out.println("Number of tasks: " + tasks.size());
		SLA sla = new TotalLatencySLA(DEADLINE);

		Collection<TaskQueue> tqs;
		//Set<List<String>> clusters = DynamicCriticalPath.schedule(DAGGenerator.getVertexWeightMap(vertices), DAGGenerator.getEdgeWeightMap(vertices));
		//tqs = DAGGenerator.clustersToTasks(vertices, clusters);
		//tqs = Actualizer.actualize(tqs);
		//System.out.println(tqs);
		//sla.printBreakdown((List<TaskQueue>) tqs);


		CCAScheduler cca = new CCAScheduler(tasks, sla);
		tqs = cca.schedule(DEADLINE);

		System.out.println(tqs);
		sla.printBreakdown((List<TaskQueue>) tqs);
		System.out.println("Done!");
		
		// build the topo sort preconditioning permutation
		int[] topoPre = new int[vertices.size()];
		for (int i = 0; i < vertices.size(); i++) {
			topoPre[i] = vertices.get(i).getID();
		}
		
		// build the schedule preconditioning premutation
		// sort the collection of taskqueues by ID of the first element, descending
		List<TaskQueue> schedule = new ArrayList<>(tqs);
		Comparator<TaskQueue> comp = ((a, b) -> b.getTasks().get(0).getID() - a.getTasks().get(0).getID());
		Collections.sort(schedule, comp);
		
		// and now build the permutation
		List<Task> flattenedSchedule = new ArrayList<>(vertices.size());
		schedule.forEach(tq -> flattenedSchedule.addAll(tq.getTasks()));
		
		int[] schedPre = new int[vertices.size()];
		for (int i = 0; i < schedPre.length; i++) {
			schedPre[i] = flattenedSchedule.get(i).getID();
		}

		ToDoubleFunction<List<double[][]>> loss = slaLoss(sla);

		jps = new MHJointPermutationLearner(new int[] { tasks.size(), tasks.size() }, loss);
		
		try {
			jps.precondition(0, MatrixUtils.preconditionedBistoch(schedPre, 0.4));
			jps.precondition(1, MatrixUtils.preconditionedBistoch(topoPre, 0.0));
		} catch (BVNException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		for (int i = 0; i < 1000000; i++) {
			if (i % 1000 == 0)
				System.out.println(i);
			jps.iterate(0);
		}

		List<double[][]> best = jps.getBest();
		tqs = permToTQs(best.get(0), best.get(1));
		System.out.println(tqs);
		sla.printBreakdown(tqs);


	}

	public List<TaskQueue> getBest() {
		return permToTQs(jps.getBest().get(0), jps.getBest().get(1));
	}
	
	private List<TaskQueue> permToTQs(double[][] d, double[][] topo) {
		int[] perm = CoeffAndMatrix.asFlatPerm(d);
		int[] topoPerm = CoeffAndMatrix.asFlatPerm(topo);

		List<Task> t = new ArrayList<>(tasks.size());
		for (int i : perm) {
			t.add(tasks.get(i));
		}

		List<TaskQueue> tqs = PermutationSolver.topoSolve(t, topoPerm);
		return tqs;
	}
	
	private double loss(List<double[][]> d) {
		List<TaskQueue> tqs = permToTQs(d.get(0), d.get(1));
		int cost = CostAnalyzer.getLatency(tqs); 
		return cost;
	}

	private ToDoubleFunction<List<double[][]>> slaLoss(SLA sla) {
		return (d -> {
			List<TaskQueue> tqs = permToTQs(d.get(0), d.get(1));
			int cost = sla.computeTotalCost(tqs);
			return cost;
		});
	}

	public static void main(String[] args) {
		BirkhoffScheduler bs = new BirkhoffScheduler();
		long t = System.currentTimeMillis();
		bs.measure(10);
		//bs.measure(18);
		//System.out.println(bs.getBest());
		System.out.println(System.currentTimeMillis() - t);
	}
}
