package com.javafiddle.revisions;

import com.javafiddle.web.services.utils.AddFileRevisionRequest;
import com.javafiddle.web.services.utils.TreeUtils;
import com.javafiddle.web.tree.IdList;
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
    
    public int addFileRevision(AddFileRevisionRequest d) {
        if (d.getId() == null || d.getTimeStamp() == null || d.getValue() == null)
            return 400;
        int id = TreeUtils.parseId(d.getId());
        if (!idList.isFile(id))
            return 400;
        
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
            Date result = df.parse(d.getTimeStamp());
            files.get(id).put(result, d.getValue());
            idList.getFile(id).setTimeStamp(result);
        } catch (ParseException ex) {
            return 400;
        }
        return 0;
    }
    
    public int saveAllFiles(List<AddFileRevisionRequest> d) {
        for (AddFileRevisionRequest temp : d) {
            if (addFileRevision(temp) == 400)
                return 400;
        }
        return 0;
    }
}
