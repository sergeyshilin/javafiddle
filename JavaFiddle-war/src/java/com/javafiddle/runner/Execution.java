package com.javafiddle.runner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Classfile for project JavaFiddleCompiler
 * Author: Sergey Shilin
 * Email: sergey.shilin@phystech.edu
 * Date: 24.11.13
 */
public class Execution implements Launcher {

    private String args = "";
    private String pathtoclass = "";
    private Process process;
    private Boolean killed = false;
    private Queue<String> stream = null;

    public Execution(String args, String pathtoclass) {
        this.stream = new LinkedList<String>();
        this.args = args;
        this.pathtoclass = pathtoclass;
    }

    public Execution(String pathtoclass) {
        this.pathtoclass = pathtoclass;
    }

    public Execution(Compilation cm) {
        try {
            cm.wait();
            pathtoclass = cm.getClassFilePath();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String command = "java " + args + " " + pathtoclass;
            process = Runtime.getRuntime().exec(command);
            printLines(" stdout:", process.getInputStream());
            printLines(" stderr:", process.getErrorStream());
            process.waitFor();
            stream.add(" exitValue() " + process.exitValue());
            stream.add("#END_OF_STREAM#");
        } catch (Exception e) {
            if(killed) {
                stream.add("<--------------------------------------------------->");
                stream.add("<------ Execution was stopped for enforcement ------>");
                stream.add("<--------------------------------------------------->");
                stream.add("#END_OF_STREAM#");
            } else {
                e.printStackTrace();
            }
        }
        synchronized(stream) {
            
        }
    }

    @Override
    public void destroy() {
        if(process != null) {
            process.destroy();
            killed = true;
        }
    }

    @Override
    public void printLines(String name, InputStream ins) throws IOException {
        String line = null;
        BufferedReader in = new BufferedReader(
                new InputStreamReader(ins));
        while ((line = in.readLine()) != null) {
            stream.add(name + " " + line);
        }
    }

    @Override
    public String getInputStream() {
        return stream.poll();
    }
    
    @Override
    public InputStream getErrorStream() {
        return process.getErrorStream();
    }

}
