package com.javafiddle.core.ejb.util;

import javax.ejb.Local;

@Local
public interface IdGeneratorLocal {
    
    long getNextId();
    
}
