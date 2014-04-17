package com.javafiddle.web.services.data;

import com.javafiddle.tree.templates.ClassTemplate;
import com.javafiddle.tree.IdList;
import com.javafiddle.tree.TreeClass;
import java.util.Date;
import java.util.TreeMap;

public class FileRevisions {
    IdList idList;
    TreeMap<Integer, TreeMap<Long, String>> files;

    public FileRevisions(IdList idList, TreeMap<Integer, TreeMap<Long, String>> files) {
        this.idList = idList;
        this.files = files;
    }
    

}
