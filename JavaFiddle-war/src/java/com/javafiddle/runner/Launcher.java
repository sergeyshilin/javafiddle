package com.javafiddle.runner;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Classfile for project JavaFiddleCompiler
 * Author: Sergey Shilin
 * Email: sergey.shilin@phystech.edu
 * Date: 25.11.13
 */
public interface Launcher {
    void run();

    void destroy();

    void printLines(String name, InputStream ins) throws IOException;

    public String getOutputStream();
    
    public OutputStream getInputStream();

    public InputStream getErrorStream();

    public Boolean streamIsEmpty();

    public void send(String input);
}
