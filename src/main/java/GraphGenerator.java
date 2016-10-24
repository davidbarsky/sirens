import edu.brandeis.ggen.GGenException;
import edu.brandeis.ggen.RandomGraphGenerator;
import edu.brandeis.ggen.graph.GGenGraph;
import edu.brandeis.ggen.graph.Vertex;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import models.Task;
import models.TaskQueue;
import models.states.BuildStatus;
import models.states.MachineType;
import util.Pair;

public class GraphGenerator {

    public Pair<ArrayList<TaskQueue>, HashMap<Integer, Task>> makeTaskQueue() throws GGenException {
        ArrayList<Vertex> ggenSource = new ArrayList<>(getErdosGNMSources());
        LinkedBlockingQueue<Vertex> queue = new LinkedBlockingQueue<>();

        for (Vertex vertex : ggenSource) {
            queue.add(vertex);
        }

        ArrayList<TaskQueue> taskQueues = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            taskQueues.add(new TaskQueue(new LinkedList<Task>(), MachineType.Small));
        }

        HashMap<Integer, Task> lookupTable = new HashMap<>(ggenSource.size());
        HashSet<Vertex> visited = new HashSet<>();
        while (!queue.isEmpty()) {
            Vertex vertex = queue.remove();
            for (Vertex child : vertex.getChildren().keySet()) {
                if (!visited.contains(child)) {
                    Task task = new Task( child.getID(), BuildStatus.NotBuilt, Optional.empty(), getTaskDependencies(child));
                    this.addToRandomTaskQueue(
                            task,
                            taskQueues
                    );
                    lookupTable.put(child.getID(), task);
                    visited.add(child);
                }
            }
        }

        return new Pair<>(taskQueues, lookupTable);
    }

    private ArrayList<Integer> getTaskDependencies(Vertex vertex) {
        ArrayList<Integer> dependencies = new ArrayList<>(vertex.getParents().keySet().size());
        for (Vertex parent : vertex.getParents().keySet()) {
            dependencies.add(parent.getID());
        }

        return dependencies;
    }

    private Collection<Vertex> getErdosGNMSources() throws GGenException {
        RandomGraphGenerator graphGenerator = new RandomGraphGenerator();
        GGenGraph graph = graphGenerator.erdosGNM(20, 0.5).generateGraph();

        return graph.getSources();
    }

    private void addToRandomTaskQueue(Task task, ArrayList<TaskQueue> taskQueues) {
        Random random = new Random();
        Integer randomInteger = random.nextInt(taskQueues.size());

        taskQueues.get(randomInteger).add(task);
    }
}
