package com.github.solayw.webutil.jpa;

import java.util.function.Function;

public class DefaultPropertyAliasMapping implements Function<String, String>
{
    @Override
    public String apply(String pro) {
        StringBuilder s = new StringBuilder();
        final int length = pro.length();
        for (int i = 0; i < length; i++) {
            char c = pro.charAt(i);
            if(Character.isUpperCase(c)) {
                if(i != 0) {
                    s.append("_");
                }
                s.append(Character.toLowerCase(c));
            } else {
                s.append(c);
            }
        }
        return s.toString();
    }
}
