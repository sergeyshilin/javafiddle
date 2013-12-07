package com.javafiddle.core.ejb.util;

import javax.ejb.Local;
import javax.inject.Named;

/**
 * Local interface for IdGenerator EJB.
 * @author danon
 */
@Local
public interface IdGeneratorLocal {
    
    long getNextId();
    
}
