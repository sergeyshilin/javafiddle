package com.javafiddle.runner;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Launcher {
    void run();

    void destroy();

    void printLines(String name, InputStream ins) throws IOException;

    public String getOutputStream();
    
    public OutputStream getInputStream();

    public InputStream getErrorStream();

    public Boolean streamIsEmpty();

    public int getExitCode();
    
    public void addToOutput(String line);

    public int waitFor();
    
    public Integer getPid();
}
