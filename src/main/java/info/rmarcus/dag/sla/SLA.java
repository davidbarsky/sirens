package info.rmarcus.dag.sla;


import com.davidbarsky.dag.CostAnalyzer;
import com.davidbarsky.dag.models.TaskQueue;

import java.util.List;

public abstract class SLA {
    public abstract int computePenalty(List<TaskQueue> tqs);

    public int computeTotalCost(List<TaskQueue> tqs) {
        int initialCost = CostAnalyzer.findCost(tqs);
        return initialCost + computePenalty(tqs);
    }


    public void printBreakdown(List<TaskQueue> tqs) {
        System.out.println("cost: " + computeTotalCost(tqs) + " penalty: " + computePenalty(tqs) + " latency: " + CostAnalyzer.getLatency(tqs));
    }

}
