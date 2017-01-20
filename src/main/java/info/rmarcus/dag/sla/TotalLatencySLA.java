package info.rmarcus.dag.sla;

import com.davidbarsky.dag.CostAnalyzer;
import com.davidbarsky.dag.models.TaskQueue;

import java.util.Collection;

public class TotalLatencySLA extends SLA {

    private int deadline;

    public TotalLatencySLA(int deadline) {
        this.deadline = deadline;
    }

    @Override
    public int computePenalty(Collection<TaskQueue> tqs) {
        int latency = CostAnalyzer.getLatency(tqs);
        if (latency > deadline)
            return (latency - deadline) * 5;

        return 0;
    }
}
