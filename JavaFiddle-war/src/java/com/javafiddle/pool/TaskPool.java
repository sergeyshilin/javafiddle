package com.javafiddle.pool;

import java.util.ArrayList;

public class TaskPool extends ArrayList<Task> {
    private static TaskPool instance;
    
    private TaskPool() { 
    }
 
    public static TaskPool getInstance() {
        if (instance == null)
            instance = new TaskPool();
     
        return instance;
    }
}
