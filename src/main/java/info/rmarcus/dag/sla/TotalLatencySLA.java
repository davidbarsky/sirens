package info.rmarcus.dag.sla;

import com.davidbarsky.dag.CostAnalyzer;
import com.davidbarsky.dag.models.TaskQueue;

import java.util.List;

public class TotalLatencySLA extends SLA {

    private int deadline;

    public TotalLatencySLA(int deadline) {
        this.deadline = deadline;
    }

    @Override
    public int computePenalty(List<TaskQueue> tqs) {
        int latency = CostAnalyzer.getLatency(tqs);
        if (latency > deadline)
            return (latency - deadline) * 5;

        return 0;
    }
}
