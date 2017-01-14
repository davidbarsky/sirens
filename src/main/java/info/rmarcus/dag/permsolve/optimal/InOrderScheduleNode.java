package info.rmarcus.dag.permsolve.optimal;

import com.davidbarsky.dag.models.Task;

import java.util.List;

public class InOrderScheduleNode extends ScheduleNode {

  public InOrderScheduleNode(List<Task> t, PruningFlyweight pf, int[][] costs) {
    super(t, pf, costs);
  }

  public InOrderScheduleNode(ScheduleNode p, StarsAndBarsNode sbn, PruningFlyweight fw) {
    super(p, sbn, fw);
  }

  @Override
  public ScheduleNode getNewChild(ScheduleNode p, StarsAndBarsNode sbn, PruningFlyweight fw) {
    return new InOrderScheduleNode(p, sbn, fw);
  }

  @Override
  public boolean isPruned() {
    return super.isPruned() || !sbn.partitionSizesIncreasing();
  }
}
