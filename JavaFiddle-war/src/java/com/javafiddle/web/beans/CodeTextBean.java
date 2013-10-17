package com.javafiddle.web.beans;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author snape
 */
@ManagedBean(name="code")
@RequestScoped
public class CodeTextBean implements Serializable {
    
    private String text = "";
    private String file = "Example";
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }

    @PostConstruct
    public void readFromFile() {
        try {
            BufferedReader br = new BufferedReader( 
                new InputStreamReader(
                    CodeTextBean.this.getClass().getResourceAsStream(file)));
            String strLine;
            while ((strLine = br.readLine()) != null)   {
               text += strLine + "\n";
            }
        } catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }
        
    }
       
}
