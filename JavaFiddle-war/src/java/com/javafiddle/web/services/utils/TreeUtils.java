package com.javafiddle.web.services.utils;

public class TreeUtils {
    public static int parseId(String idString) {
        if (idString.startsWith("node_")) {
            idString = idString.substring("node_".length());
            if (idString.endsWith("_tab"))
                idString = idString.substring(0, idString.length() - "_tab".length());
            return Integer.parseInt(idString);
        }
        return -1;
    }
}
