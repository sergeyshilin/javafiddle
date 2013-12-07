package com.javafiddle.core.ejb.util;

import javax.ejb.Local;

/**
 * Local interface for IdGenerator EJB.
 * @author danon
 */
@Local
interface IdGeneratorLocal {
    
    Long getNextId();
    
}
