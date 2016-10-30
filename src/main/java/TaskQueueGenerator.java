import info.rmarcus.ggen4j.graph.Vertex;
import models.Task;
import models.TaskQueue;
import models.states.BuildStatus;
import models.states.MachineType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Random;

public class TaskQueueGenerator {
    public ArrayList<TaskQueue> makeTaskQueues(ArrayList<Vertex> vertices) {
        ArrayList<TaskQueue> taskQueues = makeTaskQueues();

        LinkedList<Vertex> visited = new LinkedList<>();
        visited.add(vertices.get(0));
        while (!visited.isEmpty()) {
            Vertex current = visited.pop();
            for (Vertex child : current.getChildren().keySet()) {
                if (!visited.contains(child)) {
                    Task task = new Task(
                            child.getID(),
                            BuildStatus.NotBuilt,
                            Optional.empty(),
                            getDependencies(child)
                    );

                    addToRandomQueue(task, taskQueues);
                    visited.add(child);
                }
            }
        }

        return taskQueues;
    }

    private void addToRandomQueue(Task task, ArrayList<TaskQueue> taskQueues) {
        Random random = new Random();
        Integer value = random.nextInt(5);

        taskQueues.get(value).add(task);
    }

    private ArrayList<Integer> getDependencies(Vertex vertex) {
        ArrayList<Integer> dependencies = new ArrayList<>();
        for (Vertex dependency : vertex.getParents().keySet()) {
            dependencies.add(dependency.getID());
        }

        return dependencies;
    }

    private ArrayList<TaskQueue> makeTaskQueues() {
        ArrayList<TaskQueue> taskQueues = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            taskQueues.add(new TaskQueue(new LinkedList<Task>(), MachineType.Small));
        }

        return taskQueues;
    }
}

