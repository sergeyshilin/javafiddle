package com.javafiddle.pool;

import com.javafiddle.runner.Launcher;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import java.util.Date;

/**
 * Classfile for project JavaFiddleCompiler
 * Author: Sergey Shilin
 * Email: sergey.shilin@phystech.edu
 * Date: 24.11.13
 */
public class Task extends Thread implements Serializable {

    private String id = null;
    private TaskStatus status = TaskStatus.STARTING;
    private TaskType type = null;
    private Date startDate = null;
    private Date startCompilationDate = null;
    private Date endDate = null;
    private Launcher process;

    public Task(TaskType type, Launcher process) {
        this.type = type;
        this.process = process;
        this.startDate = new Date();
    }

    @Override
    public void run() {
        startCompilationDate = new Date();
        status = TaskStatus.LAUNCHED;
        try {
            process.run();
        } catch (Exception e) {
            status = TaskStatus.ERROR;
            e.printStackTrace();
        }
        finally {
            process.waitFor();
            endDate = new Date();
            status = process.getExitCode() == 0 ? TaskStatus.COMPLETED : TaskStatus.ERROR;
        }
    }

    public void kill() {
        process.destroy();
    }

    public String getOutputStream() {
        return process.getOutputStream();
    }

    public InputStream getErrorStream() {
        return process.getErrorStream();
    }
    
    public Boolean isCompleted() {
        if(status == TaskStatus.COMPLETED)
            return true;
        return false;
    }
    
    public Boolean isError() {
        if(status == TaskStatus.ERROR)
            return true;
        return false;
    }

    public Boolean streamIsEmpty() {
        return process.streamIsEmpty();
    }
    
    public Date getStartTime() {
        return startCompilationDate;
    }
    
    public Date getEndTime() {
        return endDate;
    }

    public OutputStream getInputStream() {
        return process.getInputStream();
    }

    public Launcher getProcess() {
        return process;
    }

}
