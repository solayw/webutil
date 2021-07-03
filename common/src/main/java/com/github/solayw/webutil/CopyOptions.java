package com.github.solayw.webutil;

import java.lang.reflect.Field;
import java.util.function.BiPredicate;

public class CopyOptions
{
    public boolean includePrivate = true;
    public boolean reportOnTypeDisMatch = true;
    public BiPredicate<Field, Field> filter = null;
}
