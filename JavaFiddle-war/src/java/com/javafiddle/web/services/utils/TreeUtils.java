package com.javafiddle.web.services.utils;

public class TreeUtils {
    public static int parseId(String idString) {
        if(isInteger(idString))
            return Integer.parseInt(idString);
        if (idString.startsWith("node_")) {
            idString = idString.substring("node_".length());
            if (idString.endsWith("_tab"))
                idString = idString.substring(0, idString.length() - "_tab".length());
            return Integer.parseInt(idString);
        }
        return -1;
    }
    
    public static boolean isInteger(String s) {
        try { 
            Integer.parseInt(s); 
        } catch(NumberFormatException e) { 
            return false; 
        }
        
        return true;
    }
}
