package graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import info.rmarcus.ggen4j.GGen;
import info.rmarcus.ggen4j.GGenCommand;
import info.rmarcus.ggen4j.GGenException;
import info.rmarcus.ggen4j.graph.GGenGraph;
import info.rmarcus.ggen4j.graph.Vertex;
import models.Task;
import models.TaskQueue;
import models.states.BuildStatus;
import models.states.MachineType;
import util.Pair;

public class GraphGenerator {

    public Pair<ArrayList<TaskQueue>, HashMap<Integer, Task>> makeTaskQueue() throws GGenException {
        ArrayList<Vertex> source = new ArrayList<>(getErdosGNMSources());
        LinkedBlockingQueue<Vertex> queue = new LinkedBlockingQueue<>();

        for (Vertex vertex : source) {
            queue.add(vertex);
        }

        ArrayList<TaskQueue> taskQueues = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            taskQueues.add(new TaskQueue(new LinkedList<Task>(), MachineType.Small));
        }

        HashMap<Integer, Task> lookupTable = new HashMap<>();
        HashSet<Vertex> visited = new HashSet<>();
        while (!queue.isEmpty()) {
            Vertex vertex = queue.remove();
            for (Vertex child : vertex.getChildren().keySet()) {
                if (!visited.contains(child)) {
                    Task task = new Task(child.getID(), BuildStatus.NotBuilt, Optional.empty(), getTaskDependencies(child));
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
    	GGenCommand.GGEN_PATH = System.getenv("GGEN_PATH");
        GGenGraph graph = GGen.generateGraph().erdosGNM(20, 10).generateGraph();

        return graph.getSources();
    }

    private void addToRandomTaskQueue(Task task, ArrayList<TaskQueue> taskQueues) {
        Random random = new Random();
        Integer randomInteger = random.nextInt(taskQueues.size());

        taskQueues.get(randomInteger).add(task);
    }
}
