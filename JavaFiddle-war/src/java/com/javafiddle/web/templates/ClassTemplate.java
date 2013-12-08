package com.javafiddle.web.templates;

import com.javafiddle.web.tree.IdList;
import com.javafiddle.web.tree.TreeFile;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClassTemplate {
    private String value;
    private String name;
    private String type;
    private TreeFile file;
    private IdList idList;
    
    public ClassTemplate(TreeFile file, IdList idList) {
        this.file  = file;
        this.idList = idList;
        this.name = file.getName().substring(0, file.getName().length() - 5);
        this.type = file.getType();
        String packname = idList.getPackage(file.getPackageId()).getName();
        
        value = "";
        if (!packname.startsWith("!"))
            value += "package " + packname + ";\n";
        value += getCommentsBlock();
        value += getClassDefinition();
    }
    
    private String getClassDefinition() {
        String def = "public ";
        
        switch (type) {
            case "class": case "exception": case "runnable":
                def += "class";
                break;
            case "enum":
                def += "enum";
                break;
            case "interface":
                def += "interface";
                break;
            case "annotation":
                def += "@interface";
                break;
            default:
                break;
        }
        
        def += " " + name + " ";
        
        if(type.equals("exception")) {
            def += "extends Exception ";
        }
        
        def += "{\n\t\n";
        
        if(type.equals("exception")) {
            def += getExceptionContent();
        } else if(type.equals("runnable")) {
            def += getRunnableContent();
        }
        
        def += "}";
        
        return def;
    }
    
    private String getExceptionContent() {
        String exccontent = "\t/**\n";
        exccontent += "\t * Creates a new instance of\n";
        exccontent += "\t * <code>NewException</code> without detail message.\n";
        exccontent += "\t */\n";
        
        exccontent += "\tpublic " + name + " {\n";
        exccontent += "\n";
        exccontent += "\t}\n";
        exccontent += "\t/**\n";
        exccontent += "\t * Creates a new instance of\n";
        exccontent += "\t * <code>NewException</code> with the specified detail message.\n";
        exccontent += "\t *\n";
        exccontent += "\t * @param msg the detail message\n";
        exccontent += "\t */\n";
        exccontent += "\tpublic " + name + "(String msg) {\n";
        exccontent += "\t\tsuper(msg);\n";
        exccontent += "\t}\n";
                
        return exccontent;
    }
    
     private String getRunnableContent() {
        String exccontent = "\t/**\n";
        exccontent += "\t * Creates a new instance of\n";
        exccontent += "\t * <code>NewException</code> with the specified detail message.\n";
        exccontent += "\t *\n";
        exccontent += "\t * @param args An array of arguments\n";
        exccontent += "\t */\n";
        
        exccontent += "\tpublic static void main(String[] args) {\n";
        exccontent += "\n";
        exccontent += "\t}\n";
                
        return exccontent;
    }
    
    private String getCommentsBlock() {
        String comments;
        
        comments = "\n";
        comments += "/**\n";
        comments += " * Autor: guest\n";
        comments += " * Project: " + idList.getProject(idList.getPackage(file.getPackageId()).getProjectId()).getName() + "\n";
        comments += " * Time: " + newDateTime() + "\n";
        comments += " */\n";
        
        return comments;
    }
    
    public String getValue() {
        return value;
    }

    private String newDateTime() {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date datetime = new Date();
        return df.format(datetime);
    }
}
