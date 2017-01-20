package info.rmarcus.dag.birkhoff;

import com.davidbarsky.dag.CostAnalyzer;
import com.davidbarsky.dag.DAGGenerator;
import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.TaskQueue;
import info.rmarcus.birkhoffvonneumann.CoeffAndMatrix;
import info.rmarcus.birkhoffvonneumann.learners.generalized_loss.MHJointPermutationLearner;
import info.rmarcus.dag.cca.CCAScheduler;
import info.rmarcus.dag.birkhoff.permsolve.PermutationSolver;
import info.rmarcus.dag.sla.SLA;
import info.rmarcus.dag.sla.TotalLatencySLA;
import info.rmarcus.ggen4j.graph.Vertex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.ToDoubleFunction;

public class BirkhoffScheduler {

	private List<Task> tasks;
	private MHJointPermutationLearner jps;

	private final static int DEADLINE = 2500;


	public void measure(int n) {
		Collection<Vertex> vertices = DAGGenerator.getSparseLU(n);
		tasks = DAGGenerator.verticesToTasks(vertices);
				
		System.out.println("Number of tasks: " + tasks.size());
		SLA sla = new TotalLatencySLA(DEADLINE);


		/*Set<List<String>> clusters = DynamicCriticalPath.schedule(DAGGenerator.getVertexWeightMap(vertices), DAGGenerator.getEdgeWeightMap(vertices));
		Collection<TaskQueue> tqs = DAGGenerator.clustersToTasks(vertices, clusters);
		tqs = Actualizer.actualize(tqs);
		System.out.println(tqs);
		sla.printBreakdown((List<TaskQueue>) tqs);*/

		CCAScheduler cca = new CCAScheduler(tasks, sla);
		Collection<TaskQueue> tqs = cca.schedule(DEADLINE);

		System.out.println(tqs);
		sla.printBreakdown((List<TaskQueue>) tqs);
		System.out.println("Done!");

		ToDoubleFunction<List<double[][]>> loss = slaLoss(sla);

		jps = new MHJointPermutationLearner(new int[] { tasks.size(), tasks.size() }, loss);
		for (int i = 0; i < 1000000; i++) {
			if (i % 1000 == 0)
				System.out.println(i);
			jps.iterate();
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
		bs.measure(8);
		//bs.measure(18);
		//System.out.println(bs.getBest());
		System.out.println(System.currentTimeMillis() - t);
	}
}
