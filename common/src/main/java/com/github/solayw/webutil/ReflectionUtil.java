package com.github.solayw.webutil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ReflectionUtil
{
    public static List<Field> fields(Class type,
                                     boolean includeStatic,
                                     boolean includeNonPublic,
                                     boolean includeFinal,
                                     boolean includeSuper,
                                     boolean includeTransient,
                                     Predicate<Field> filter) {
        ArrayList<Field> res = new ArrayList<>();
        while (type != Object.class) {
            for (Field f : type.getDeclaredFields()) {
                final int modifiers = f.getModifiers();
                if(!includeStatic && Modifier.isStatic(modifiers)) {
                    continue;
                }
                if(!includeNonPublic && !Modifier.isPublic(modifiers)) {
                    continue;
                }
                if(!includeFinal && Modifier.isFinal(modifiers)) {
                    continue;
                }
                if(!includeTransient && Modifier.isTransient(modifiers)) {
                    continue;
                }
                if(filter != null && !filter.test(f)) {
                    continue;
                }
                f.setAccessible(true);
                res.add(f);
            }
            if(includeSuper) {
                type = type.getSuperclass();
            } else {
                break;
            }
        }
        return res;
    }
}
