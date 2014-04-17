package com.javafiddle.web.services.data;

import static com.javafiddle.web.services.data.SessionData.SEP;
import com.javafiddle.tree.IdList;
import com.javafiddle.tree.Tree;
import com.javafiddle.tree.TreeClass;
import java.io.File;
import java.io.Serializable;
import java.util.TreeMap;

public interface ISessionData extends Serializable {
    static final String SEP = File.separator;
    static final String PREFIX = System.getProperty("user.home") + SEP + "javafiddle_data";
    static final String BUILD = PREFIX + SEP + "build";
       
    public void resetData();
     
    public Tree getTree();

    public void setTree(Tree tree);

    public IdList getIdList();

    public void setIdList(IdList idList);

    void addFileRevision(TreeClass file, IdList idList);
    
    long addFileRevision(long id, long timeStamp, String value);
}
