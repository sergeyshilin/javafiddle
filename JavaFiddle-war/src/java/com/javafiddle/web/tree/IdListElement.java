package com.javafiddle.web.tree;

public class IdListElement {
    Object object; 
    IdNodeType idNodeType;

    public IdListElement(IdNodeType idNodeType, Object object) {
        this.object = object;
        this.idNodeType = idNodeType;
    }
    
    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public IdNodeType getIdNodeType() {
        return idNodeType;
    }

    public void setIdNodeType(IdNodeType idNodeType) {
        this.idNodeType = idNodeType;
    }   
}
