package com.javafiddle.web.codemirror;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
 
@Named("history")
@SessionScoped
public class MirrorSessionBean implements Serializable {
    Map<String, FileEditions> values;

    public MirrorSessionBean() {
        values = new TreeMap<>();
    }
    
    public FileEditions list(String id) {
       if (!values.containsKey(id))
            values.put(id, new FileEditions());
        return values.get(id);
    }

}
