package com.project.furnishyourhome.extensionMethods;

import java.util.Collection;
import java.util.Iterator;
import java.lang.String;

/**
 * Created by Andrey on 5.5.2015 ã..
 */
public class StringExtensions{
    public static String join(String delimiter, Collection<?> s) {
        StringBuilder builder = new StringBuilder();
        Iterator<?> iter = s.iterator();
        while (iter.hasNext()) {
            builder.append(iter.next());
            if (!iter.hasNext()) {
                break;
            }
            builder.append(delimiter);
        }
        return builder.toString();
    }
}
