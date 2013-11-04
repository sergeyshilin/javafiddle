package com.javafiddle.web.codemirror;

public class SourceFile {
    String name;
    String id;
    String type;
    String timeRevision;

    // abstract for tree
    public SourceFile(String name, String type) {
        this.name = name;
        this.type = type;
    }
        
    public SourceFile(String name, String id, String pack, String timeRevision) {
        this.name = name;
        this.id = id;
        this.type = pack;
        this.timeRevision = timeRevision;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimeRevision() {
        return timeRevision;
    }

    public void setTimeRevision(String timeRevision) {
        this.timeRevision = timeRevision;
    }
}
