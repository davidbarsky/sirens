import edu.brandeis.ggen.GGenException;
import models.Task;
import models.TaskQueue;
import util.Pair;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void main(String... args) {
        try {
            Pair<ArrayList<TaskQueue>, HashMap<Integer, Task>> pair = makeTaskQueue();

            ArrayList<TaskQueue> taskQueues = pair.firstValue;
            HashMap<Integer, Task> lookupTable = pair.secondValue;

            ArrayList<Task> schedule = buildGraph(taskQueues, lookupTable);
            schedule.forEach(t -> System.out.println(t.ID));
        } catch (GGenException e) {
            System.out.println(e);
        }
    }

    private static Pair<ArrayList<TaskQueue>, HashMap<Integer, Task>> makeTaskQueue() throws GGenException {
         GraphGenerator graphGenerator = new GraphGenerator();
         return graphGenerator.makeTaskQueue();
    }

    private static ArrayList<Task> buildGraph(ArrayList<TaskQueue> taskQueues, HashMap<Integer, Task> lookupTable) {
        Actualizer actualizer = new Actualizer(lookupTable);

        ArrayList<Task> schedule = actualizer.makeSchedule(taskQueues);
        return schedule;
    }
}
