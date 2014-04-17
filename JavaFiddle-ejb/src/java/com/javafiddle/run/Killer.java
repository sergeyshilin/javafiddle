/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javafiddle.run;

import com.javafiddle.run.pool.Task;
import com.javafiddle.run.pool.TaskType;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Killer extends Thread implements Serializable {
    private Task task = null;
    private Integer pid = null;
    private String resultmsg = null;
    private final long EXECUTE_WAIT_TIME = 30000;
    private final long COMPILE_WAIT_TIME = 15000;
    
    public Killer(Task tasktokill) {
        task = tasktokill;
    }
    
    public Killer(Integer pidtokill) {
        pid = pidtokill;
    }
    
    @Override
    public void run() {
        try {
            Thread.sleep((task.getType() == TaskType.EXECUTION) ? EXECUTE_WAIT_TIME - 1000 : COMPILE_WAIT_TIME - 1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Killer.class.getName()).log(Level.SEVERE, null, ex);
        }
                
        if(!task.isCompleted()) {
            task.addToOutput("<-- TIME IS OUT -->");
            task.addToOutput("#END_OF_STREAM#");
            task.kill();
        }
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Killer.class.getName()).log(Level.SEVERE, null, ex);
        }

        if(!task.isCompleted()) {
            try {
                int s = Runtime.getRuntime().exec("kill " + task.getPid()).waitFor();
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(Killer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
