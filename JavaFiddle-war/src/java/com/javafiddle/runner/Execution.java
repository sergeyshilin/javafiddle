package com.javafiddle.runner;

import com.javafiddle.saving.SavingFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        this.stream = new LinkedList<>();
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
        } catch (InterruptedException ex) {
            Logger.getLogger(Execution.class.getName()).log(Level.SEVERE, null, ex);
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
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(Execution.class.getName()).log(Level.SEVERE, null, ex);
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
    public String getOutputStream() {
        return stream.poll();
    }
    
    @Override
    public OutputStream getInputStream() {
        return process.getOutputStream();
    }
    
    @Override
    public InputStream getErrorStream() {
        return process.getErrorStream();
    }

    @Override
    public Boolean streamIsEmpty() {
        return stream.isEmpty();
    }

    @Override
    public void send(String input) {
        
    }

    @Override
    public int getExitCode() {
        return process.exitValue();
    }
    
    @Override
    public int waitFor() {
        try {
            return process.waitFor();
        } catch (InterruptedException ex) {
            Logger.getLogger(Compilation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

}
