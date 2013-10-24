package com.javafiddle.web.beans;

import com.javafiddle.web.utils.JSFHelper;
import java.io.Serializable;
import javax.faces.context.FacesContext;

/**
 * JSF Managed beans and CDI beans should extend this class.
 * @author danshin
 */
public class CommonBean implements Serializable {
    
    protected <T> T getSessionAttribute(String attribute) {
        return (T) getJSFHelper().getSessionAttribute(Object.class, attribute);
    }
    
    protected Long getCurrentUserId() {
        return getJSFHelper().getCurrentUserId();
    }
    
    protected JSFHelper getJSFHelper() {
        return new JSFHelper(FacesContext.getCurrentInstance());
    }
    
}
