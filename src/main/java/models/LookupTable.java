package models;

import models.states.BuildStatus;

import java.util.HashMap;

public class LookupTable {
    private static HashMap<Integer, Task> lookupTable;

    public static void addTask(Integer ID, Task task) {
        lookupTable.put(ID, task);
    }

    public static Boolean isBuilt(Integer ID) {
        Task task = lookupTable.get(ID);
        if (task == null) {
            return false;
        }

        return task.buildStatus == BuildStatus.Built;
    }

    public static void clear() {
        lookupTable.clear();
    }
}
