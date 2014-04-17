package com.javafiddle.web.services.data;

import com.javafiddle.tree.IdList;
import com.javafiddle.tree.Tree;
import com.javafiddle.tree.TreeClass;
import com.javafiddle.tree.templates.ClassTemplate;
import java.util.Date;
import java.util.TreeMap;
import javax.enterprise.context.SessionScoped;

@SessionScoped
public class SessionData implements ISessionData {
    
    Tree tree;
    IdList idList;
    TreeMap<Long, TreeMap<Long, String>> files;

    public SessionData() {
        reset();
    }

    private void reset() {
        idList = new IdList();
        tree = new Tree();
        files = new TreeMap<>();
    }
    
    @Override
    public void resetData() {
        reset();
    }
    
    @Override
    public Tree getTree() {
        return tree;
    }

    @Override
    public void setTree(Tree tree) {
        this.tree = tree;
    }

    @Override
    public IdList getIdList() {
        return idList;
    }

    @Override
    public void setIdList(IdList idList) {
        this.idList = idList;
    }

    @Override
    public void addFileRevision(TreeClass file, IdList idList) {
        addFileRevision(file.getId(), 0, new ClassTemplate(file, idList).getValue());
    }
    
    public long addFileRevision(long id, long timeStamp, String value) {
        if (!idList.isClass(id))
            return 400;
        
        if (!files.containsKey(id))
            files.put(id, new TreeMap<Long, String>());
        else {
            long old = idList.getClass(id).getTimeStamp();
            String oldText = files.get(id).get(old);
            if (oldText.hashCode() == value.hashCode())
                return 304;
        }
        long result = timeStamp == 0 ? new Date().getTime() : timeStamp;
        files.get(id).put(result, value);
        idList.getClass(id).setTimeStamp(result);
        return 200;
    }
}
