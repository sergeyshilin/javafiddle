package com.javafiddle.revisions;

import com.javafiddle.saving.SavingFile;
import com.javafiddle.web.services.utils.AddFileRevisionRequest;
import com.javafiddle.web.services.utils.TreeUtils;
import com.javafiddle.web.templates.ClassTemplate;
import com.javafiddle.web.tree.IdList;
import com.javafiddle.web.tree.TreeFile;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

public class Revisions {
    IdList idList;
    TreeMap<Integer, TreeMap<Date, String>> files;

    public Revisions(IdList idList, TreeMap<Integer, TreeMap<Date, String>> files) {
        this.idList = idList;
        this.files = files;
    }
    
    public void addFileRevision(TreeFile file, IdList idList) {
        String id = new Integer(file.getId()).toString();

        System.out.println(addFileRevision(
                new AddFileRevisionRequest(id, null, 
                    new ClassTemplate(file, idList).getValue())));
    }
    
    public int addFileRevision(AddFileRevisionRequest d) {
        if (d.getId() == null || d.getValue() == null) {
            return 400;
        }
        
        int id = TreeUtils.parseId(d.getId());
        if (!idList.isFile(id)) {
            System.out.println("not a file");
            return 400;
        }
        
        if (!files.containsKey(id))
            files.put(id, new TreeMap<Date, String>());
        else {
            Date old = idList.getFile(id).getTimeStamp();
            String oldText = files.get(id).get(old);
            if (oldText.hashCode() == d.getValue().hashCode())
                return 304;
        }
        try {
            DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            Date result = (d.getTimeStamp() == null) ? new Date() : df.parse(d.getTimeStamp());
            files.get(id).put(result, d.getValue());
            idList.getFile(id).setTimeStamp(result);
            System.out.println(idList.getFile(id).getTimeStamp());
        } catch (ParseException ex) {
            return 400;
        }
        return 200;
    }
    
    public int saveAllFiles(List<AddFileRevisionRequest> d) {
        for (AddFileRevisionRequest temp : d) {
            if (addFileRevision(temp) == 400)
                return 400;
        }
        return 200;
    }
}
