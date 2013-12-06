package com.javafiddle.runner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
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
public class Compilation implements Launcher {

    private String filepath = "";
    private String args = "";
    private String classFilePath = null;
    private Process process;
    private Boolean killed = false;
    private Queue<String> stream = null;

    public Compilation(String args, String pathtofile) {
        this.stream = new LinkedList<String>();
        this.filepath = pathtofile;
        this.args = args;
    }

    public Compilation(String pathtofile) {
        this.stream = new LinkedList<>();
        this.filepath = pathtofile;
    }

    @Override
    public void run() {
        try {
            String command = "javac " + args + " " + filepath;
            process = Runtime.getRuntime().exec(command);
            printLines(" stdout:", process.getInputStream());
            printLines(" <span style='color:red'>stderr:</span>", process.getErrorStream());
            process.waitFor();
            stream.add(" exitValue() " + process.exitValue());
            stream.add("#END_OF_STREAM#");
        } catch (Exception e) {
            if(killed) {
                stream.add("<----------------------------------------------------->");
                stream.add("<------ Compilation was stopped for enforcement ------>");
                stream.add("<----------------------------------------------------->");
                stream.add("#END_OF_STREAM#");
            } else {
                e.printStackTrace();
            }
        }
        finally {
            makeClassFilePath();
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

    private void makeClassFilePath() {
        classFilePath = filepath.replace('/', '.').substring(0, filepath.indexOf(".java"));
    }

    public String getClassFilePath() {
        return classFilePath;
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
        try {
            process.getOutputStream().write(input.getBytes(Charset.forName("UTF-8")));
        } catch (IOException ex) {
            Logger.getLogger(Compilation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
