package info.rmarcus.dag.sla;


import com.davidbarsky.dag.CostAnalyzer;
import com.davidbarsky.dag.models.TaskQueue;

import java.util.Collection;

public abstract class SLA {
    public abstract int computePenalty(Collection<TaskQueue> tqs);

    public int computeTotalCost(Collection<TaskQueue> tqs) {
        int initialCost = CostAnalyzer.findCost(tqs);
        return initialCost + computePenalty(tqs);
    }


    public void printBreakdown(Collection<TaskQueue> tqs) {
        System.out.println("cost: " + computeTotalCost(tqs) + " penalty: " + computePenalty(tqs) + " latency: " + CostAnalyzer.getLatency(tqs));
    }

}
