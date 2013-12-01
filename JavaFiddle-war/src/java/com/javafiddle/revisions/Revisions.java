package com.javafiddle.revisions;

import com.javafiddle.web.templates.ClassTemplate;
import com.javafiddle.web.tree.IdList;
import com.javafiddle.web.tree.TreeFile;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

public class Revisions {
    IdList idList;
    TreeMap<Integer, TreeMap<Date, String>> files;

    public Revisions(IdList idList, TreeMap<Integer, TreeMap<Date, String>> files) {
        this.idList = idList;
        this.files = files;
    }
    
    public void addFileRevision(TreeFile file, IdList idList) {
        addFileRevision(file.getId(), null, new ClassTemplate(file, idList).getValue());
    }
    
    public int addFileRevision(int id, String timeStamp, String value) {
        if (!idList.isFile(id))
            return 400;
        
        if (!files.containsKey(id))
            files.put(id, new TreeMap<Date, String>());
        else {
            Date old = idList.getFile(id).getTimeStamp();
            String oldText = files.get(id).get(old);
            if (oldText.hashCode() == value.hashCode())
                return 304;
        }
        try {
            DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            Date result = (timeStamp == null) ? new Date() : df.parse(timeStamp);
            files.get(id).put(result, value);
            idList.getFile(id).setTimeStamp(result);
        } catch (ParseException ex) {
            return 400;
        }
        return 200;
    }
}
