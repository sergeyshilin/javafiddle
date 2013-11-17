package com.javafiddle.web.services.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TreeUtils {
    public static int parseId(String idString) {
        Pattern pattern = Pattern.compile("node_([0-9]+)");
        Matcher matcher = pattern.matcher(idString);
        if (matcher.matches())
             return Integer.parseInt(matcher.group(1));
        else {
            pattern = Pattern.compile("node_([0-9]+)_tab");
            matcher = pattern.matcher(idString);
            if (matcher.matches())
                return Integer.parseInt(matcher.group(1));
        }
        return -1;
    }
}
