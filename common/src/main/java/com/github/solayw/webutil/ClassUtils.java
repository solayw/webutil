/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.solayw.webutil;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;

import static com.github.solayw.webutil.Streams.filterAll;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;

/**
 * ClassUtils. (Tool, Static, ThreadSafe)
 */
public class ClassUtils
{

    public static final String CLASS_EXTENSION = ".class";

    public static final String JAVA_EXTENSION = ".java";
    private static final int JIT_LIMIT = 5 * 1024;

    private ClassUtils() {
    }



    public static Class<?> forName(String[] packages, String className) {
        try {
            return classForName(className);
        } catch (ClassNotFoundException e) {
            if (packages != null && packages.length > 0) {
                for (String pkg : packages) {
                    try {
                        return classForName(pkg + "." + className);
                    } catch (ClassNotFoundException ignore) {
                    }
                }
            }
            throw new IllegalStateException(e.getMessage(), e);
        }
    }



    public static Class<?> classForName(String className) throws ClassNotFoundException {
        switch (className) {
            case "boolean":
                return boolean.class;
            case "byte":
                return byte.class;
            case "char":
                return char.class;
            case "short":
                return short.class;
            case "int":
                return int.class;
            case "long":
                return long.class;
            case "float":
                return float.class;
            case "double":
                return double.class;
            case "boolean[]":
                return boolean[].class;
            case "byte[]":
                return byte[].class;
            case "char[]":
                return char[].class;
            case "short[]":
                return short[].class;
            case "int[]":
                return int[].class;
            case "long[]":
                return long[].class;
            case "float[]":
                return float[].class;
            case "double[]":
                return double[].class;
            default:
        }
        try {
            return arrayForName(className);
        } catch (ClassNotFoundException e) {
            // try to load from java.lang package
            if (className.indexOf('.') == -1) {
                try {
                    return arrayForName("java.lang." + className);
                } catch (ClassNotFoundException ignore) {
                    // ignore, let the original exception be thrown
                }
            }
            throw e;
        }
    }

    private static Class<?> arrayForName(String className) throws ClassNotFoundException {
        return Class.forName(className.endsWith("[]")
                ? "[L" + className.substring(0, className.length() - 2) + ";"
                : className, true, Thread.currentThread().getContextClassLoader());
    }

    public static Class<?> getBoxedClass(Class<?> type) {
        if (type == boolean.class) {
            return Boolean.class;
        } else if (type == char.class) {
            return Character.class;
        } else if (type == byte.class) {
            return Byte.class;
        } else if (type == short.class) {
            return Short.class;
        } else if (type == int.class) {
            return Integer.class;
        } else if (type == long.class) {
            return Long.class;
        } else if (type == float.class) {
            return Float.class;
        } else if (type == double.class) {
            return Double.class;
        } else {
            return type;
        }
    }

    public static Boolean boxed(boolean v) {
        return Boolean.valueOf(v);
    }

    public static Character boxed(char v) {
        return Character.valueOf(v);
    }

    public static Byte boxed(byte v) {
        return Byte.valueOf(v);
    }

    public static Short boxed(short v) {
        return Short.valueOf(v);
    }

    public static Integer boxed(int v) {
        return Integer.valueOf(v);
    }

    public static Long boxed(long v) {
        return Long.valueOf(v);
    }

    public static Float boxed(float v) {
        return Float.valueOf(v);
    }

    public static Double boxed(double v) {
        return Double.valueOf(v);
    }

    public static Object boxed(Object v) {
        return v;
    }

    public static boolean unboxed(Boolean v) {
        return v == null ? false : v.booleanValue();
    }

    public static char unboxed(Character v) {
        return v == null ? '\0' : v.charValue();
    }

    public static byte unboxed(Byte v) {
        return v == null ? 0 : v.byteValue();
    }

    public static short unboxed(Short v) {
        return v == null ? 0 : v.shortValue();
    }

    public static int unboxed(Integer v) {
        return v == null ? 0 : v.intValue();
    }

    public static long unboxed(Long v) {
        return v == null ? 0 : v.longValue();
    }

    public static float unboxed(Float v) {
        return v == null ? 0 : v.floatValue();
    }

    public static double unboxed(Double v) {
        return v == null ? 0 : v.doubleValue();
    }

    public static Object unboxed(Object v) {
        return v;
    }

    public static boolean isNotEmpty(Object object) {
        return getSize(object) > 0;
    }

    public static int getSize(Object object) {
        if (object == null) {
            return 0;
        }
        if (object instanceof Collection<?>) {
            return ((Collection<?>) object).size();
        } else if (object instanceof Map<?, ?>) {
            return ((Map<?, ?>) object).size();
        } else if (object.getClass().isArray()) {
            return Array.getLength(object);
        } else {
            return -1;
        }
    }

    public static URI toURI(String name) {
        try {
            return new URI(name);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<?> getGenericClass(Class<?> cls) {
        return getGenericClass(cls, 0);
    }

    public static Class<?> getGenericClass(Class<?> cls, int i) {
        try {
            ParameterizedType parameterizedType = ((ParameterizedType) cls.getGenericInterfaces()[0]);
            Object genericClass = parameterizedType.getActualTypeArguments()[i];
            if (genericClass instanceof ParameterizedType) {
                return (Class<?>) ((ParameterizedType) genericClass).getRawType();
            } else if (genericClass instanceof GenericArrayType) {
                Type type = ((GenericArrayType) genericClass).getGenericComponentType();
                if (type instanceof TypeVariable) {
                    return type.getClass();
                }
                return (((GenericArrayType) genericClass).getGenericComponentType() instanceof Class<?>)
                        ? (Class<?>) ((GenericArrayType) genericClass).getGenericComponentType()
                        : ((GenericArrayType) genericClass).getGenericComponentType().getClass();
            } else if (genericClass != null) {
                if (genericClass instanceof TypeVariable) {
                    return genericClass.getClass();
                }
                return (Class<?>) genericClass;
            }
        } catch (Throwable e) {

        }
        if (cls.getSuperclass() != null) {
            return getGenericClass(cls.getSuperclass(), i);
        } else {
            throw new IllegalArgumentException(cls.getName() + " generic type undefined!");
        }
    }




    public static String toString(Throwable e) {
        StringWriter w = new StringWriter();
        PrintWriter p = new PrintWriter(w);
        p.print(e.getClass().getName() + ": ");
        if (e.getMessage() != null) {
            p.print(e.getMessage() + "\n");
        }
        p.println();
        try {
            e.printStackTrace(p);
            return w.toString();
        } finally {
            p.close();
        }
    }

    public static void checkBytecode(String name, byte[] bytecode) {
        if (bytecode.length > JIT_LIMIT) {
            System.err.println("The template bytecode too long, may be affect the JIT compiler. template class: " + name);
        }
    }

    public static String getSizeMethod(Class<?> cls) {
        try {
            return cls.getMethod("size", new Class<?>[0]).getName() + "()";
        } catch (NoSuchMethodException e) {
            try {
                return cls.getMethod("length", new Class<?>[0]).getName() + "()";
            } catch (NoSuchMethodException e2) {
                try {
                    return cls.getMethod("getSize", new Class<?>[0]).getName() + "()";
                } catch (NoSuchMethodException e3) {
                    try {
                        return cls.getMethod("getLength", new Class<?>[0]).getName() + "()";
                    } catch (NoSuchMethodException e4) {
                        return null;
                    }
                }
            }
        }
    }

    public static String getMethodName(Method method, Class<?>[] parameterClasses, String rightCode) {
        if (method.getParameterTypes().length > parameterClasses.length) {
            Class<?>[] types = method.getParameterTypes();
            StringBuilder buf = new StringBuilder(rightCode);
            for (int i = parameterClasses.length; i < types.length; i++) {
                if (buf.length() > 0) {
                    buf.append(",");
                }
                Class<?> type = types[i];
                String def;
                if (type == boolean.class) {
                    def = "false";
                } else if (type == char.class) {
                    def = "\'\\0\'";
                } else if (type == byte.class
                        || type == short.class
                        || type == int.class
                        || type == long.class
                        || type == float.class
                        || type == double.class) {
                    def = "0";
                } else {
                    def = "null";
                }
                buf.append(def);
            }
        }
        return method.getName() + "(" + rightCode + ")";
    }

    public static Method searchMethod(Class<?> currentClass, String name, Class<?>[] parameterTypes) throws NoSuchMethodException {
        if (currentClass == null) {
            throw new NoSuchMethodException("class == null");
        }
        try {
            return currentClass.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            for (Method method : currentClass.getMethods()) {
                if (method.getName().equals(name)
                        && parameterTypes.length == method.getParameterTypes().length
                        && Modifier.isPublic(method.getModifiers())) {
                    if (parameterTypes.length > 0) {
                        Class<?>[] types = method.getParameterTypes();
                        boolean match = true;
                        for (int i = 0; i < parameterTypes.length; i++) {
                            if (!types[i].isAssignableFrom(parameterTypes[i])) {
                                match = false;
                                break;
                            }
                        }
                        if (!match) {
                            continue;
                        }
                    }
                    return method;
                }
            }
            throw e;
        }
    }

    public static String getInitCode(Class<?> type) {
        if (byte.class.equals(type)
                || short.class.equals(type)
                || int.class.equals(type)
                || long.class.equals(type)
                || float.class.equals(type)
                || double.class.equals(type)) {
            return "0";
        } else if (char.class.equals(type)) {
            return "'\\0'";
        } else if (boolean.class.equals(type)) {
            return "false";
        } else {
            return "null";
        }
    }

    public static <K, V> Map<K, V> toMap(Map.Entry<K, V>[] entries) {
        Map<K, V> map = new HashMap<K, V>();
        if (entries != null && entries.length > 0) {
            for (Map.Entry<K, V> entry : entries) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return map;
    }

    /**
     * get simple class name from qualified class name
     */
    public static String getSimpleClassName(String qualifiedName) {
        if (null == qualifiedName) {
            return null;
        }
        int i = qualifiedName.lastIndexOf('.');
        return i < 0 ? qualifiedName : qualifiedName.substring(i + 1);
    }
    public static final String ARRAY_SUFFIX = "[]";
    /**
     * Simple Types including:
     * <ul>
     *     <li>{@link Void}</li>
     *     <li>{@link Boolean}</li>
     *     <li>{@link Character}</li>
     *     <li>{@link Byte}</li>
     *     <li>{@link Integer}</li>
     *     <li>{@link Float}</li>
     *     <li>{@link Double}</li>
     *     <li>{@link String}</li>
     *     <li>{@link BigDecimal}</li>
     *     <li>{@link BigInteger}</li>
     *     <li>{@link Date}</li>
     *     <li>{@link Object}</li>
     * </ul>
     *
     * @see javax.management.openmbean.SimpleType
     * @since 2.7.6
     */
    public static final Set<Class<?>> SIMPLE_TYPES = new HashSet<>(
            Arrays.asList(
                    Void.class,
                    Boolean.class,
                    Character.class,
                    Byte.class,
                    Short.class,
                    Integer.class,
                    Long.class,
                    Float.class,
                    Double.class,
                    String.class,
                    BigDecimal.class,
                    BigInteger.class,
                    Date.class,
                    Object.class
            )
    );
    /**
     * Prefix for internal array class names: "[L"
     */
    private static final String INTERNAL_ARRAY_PREFIX = "[L";
    /**
     * Map with primitive type name as key and corresponding primitive type as
     * value, for example: "int" -> "int.class".
     */
    private static final Map<String, Class<?>> PRIMITIVE_TYPE_NAME_MAP = new HashMap<String, Class<?>>(32);
    /**
     * Map with primitive wrapper type as key and corresponding primitive type
     * as value, for example: Integer.class -> int.class.
     */
    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPER_TYPE_MAP = new HashMap<Class<?>, Class<?>>(16);
    private static final char PACKAGE_SEPARATOR_CHAR = '.';

    static {
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Boolean.class, boolean.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Byte.class, byte.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Character.class, char.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Double.class, double.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Float.class, float.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Integer.class, int.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Long.class, long.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Short.class, short.class);

        Set<Class<?>> primitiveTypeNames = new HashSet<>(32);
        primitiveTypeNames.addAll(PRIMITIVE_WRAPPER_TYPE_MAP.values());
        primitiveTypeNames.addAll(Arrays
                .asList(boolean[].class, byte[].class, char[].class, double[].class,
                        float[].class, int[].class, long[].class, short[].class));
        for (Class<?> primitiveTypeName : primitiveTypeNames) {
            PRIMITIVE_TYPE_NAME_MAP.put(primitiveTypeName.getName(), primitiveTypeName);
        }
    }

    public static Class<?> forNameWithThreadContextClassLoader(String name)
            throws ClassNotFoundException {
        return forName(name, Thread.currentThread().getContextClassLoader());
    }

    public static Class<?> forNameWithCallerClassLoader(String name, Class<?> caller)
            throws ClassNotFoundException {
        return forName(name, caller.getClassLoader());
    }

    public static ClassLoader getCallerClassLoader(Class<?> caller) {
        return caller.getClassLoader();
    }

    /**
     * get class loader
     *
     * @param clazz
     * @return class loader
     */
    public static ClassLoader getClassLoader(Class<?> clazz) {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back to system class loader...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = clazz.getClassLoader();
            if (cl == null) {
                // getClassLoader() returning null indicates the bootstrap ClassLoader
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ex) {
                    // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                }
            }
        }

        return cl;
    }

    /**
     * Return the default ClassLoader to use: typically the thread context
     * ClassLoader, if available; the ClassLoader that loaded the ClassUtils
     * class will be used as fallback.
     * <p>
     * Call this method if you intend to use the thread context ClassLoader in a
     * scenario where you absolutely need a non-null ClassLoader reference: for
     * example, for class path resource loading (but not necessarily for
     * <code>Class.forName</code>, which accepts a <code>null</code> ClassLoader
     * reference as well).
     *
     * @return the default ClassLoader (never <code>null</code>)
     * @see Thread#getContextClassLoader()
     */
    public static ClassLoader getClassLoader() {
        return getClassLoader(ClassUtils.class);
    }

    /**
     * Same as <code>Class.forName()</code>, except that it works for primitive
     * types.
     */
    public static Class<?> forName(String name) throws ClassNotFoundException {
        return forName(name, getClassLoader());
    }

    /**
     * Replacement for <code>Class.forName()</code> that also returns Class
     * instances for primitives (like "int") and array class names (like
     * "String[]").
     *
     * @param name        the name of the Class
     * @param classLoader the class loader to use (may be <code>null</code>,
     *                    which indicates the default class loader)
     * @return Class instance for the supplied name
     * @throws ClassNotFoundException if the class was not found
     * @throws LinkageError           if the class file could not be loaded
     * @see Class#forName(String, boolean, ClassLoader)
     */
    public static Class<?> forName(String name, ClassLoader classLoader)
            throws ClassNotFoundException, LinkageError {

        Class<?> clazz = resolvePrimitiveClassName(name);
        if (clazz != null) {
            return clazz;
        }

        // "java.lang.String[]" style arrays
        if (name.endsWith(ARRAY_SUFFIX)) {
            String elementClassName = name.substring(0, name.length() - ARRAY_SUFFIX.length());
            Class<?> elementClass = forName(elementClassName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }

        // "[Ljava.lang.String;" style arrays
        int internalArrayMarker = name.indexOf(INTERNAL_ARRAY_PREFIX);
        if (internalArrayMarker != -1 && name.endsWith(";")) {
            String elementClassName = null;
            if (internalArrayMarker == 0) {
                elementClassName = name
                        .substring(INTERNAL_ARRAY_PREFIX.length(), name.length() - 1);
            } else if (name.startsWith("[")) {
                elementClassName = name.substring(1);
            }
            Class<?> elementClass = forName(elementClassName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }

        ClassLoader classLoaderToUse = classLoader;
        if (classLoaderToUse == null) {
            classLoaderToUse = getClassLoader();
        }
        return classLoaderToUse.loadClass(name);
    }

    /**
     * Resolve the given class name as primitive class, if appropriate,
     * according to the JVM's naming rules for primitive classes.
     * <p>
     * Also supports the JVM's internal class names for primitive arrays. Does
     * <i>not</i> support the "[]" suffix notation for primitive arrays; this is
     * only supported by {@link #forName}.
     *
     * @param name the name of the potentially primitive class
     * @return the primitive class, or <code>null</code> if the name does not
     * denote a primitive class or primitive array class
     */
    public static Class<?> resolvePrimitiveClassName(String name) {
        Class<?> result = null;
        // Most class names will be quite long, considering that they
        // SHOULD sit in a package, so a length check is worthwhile.
        if (name != null && name.length() <= 8) {
            // Could be a primitive - likely.
            result = (Class<?>) PRIMITIVE_TYPE_NAME_MAP.get(name);
        }
        return result;
    }

    public static String toShortString(Object obj) {
        if (obj == null) {
            return "null";
        }
        return obj.getClass().getSimpleName() + "@" + System.identityHashCode(obj);

    }

    public static String simpleClassName(Class<?> clazz) {
        if (clazz == null) {
            throw new NullPointerException("clazz");
        }
        String className = clazz.getName();
        final int lastDotIdx = className.lastIndexOf(PACKAGE_SEPARATOR_CHAR);
        if (lastDotIdx > -1) {
            return className.substring(lastDotIdx + 1);
        }
        return className;
    }


    /**
     * The specified type is primitive type or simple type
     *
     * @param type the type to test
     * @return
     * @deprecated as 2.7.6, use {@link Class#isPrimitive()} plus {@link #isSimpleType(Class)} instead
     */
    @Deprecated
    public static boolean isPrimitive(Class<?> type) {
        return type != null && (type.isPrimitive() || isSimpleType(type));
    }

    /**
     * The specified type is simple type or not
     *
     * @param type the type to test
     * @return if <code>type</code> is one element of {@link #SIMPLE_TYPES}, return <code>true</code>, or <code>false</code>
     * @see #SIMPLE_TYPES
     * @since 2.7.6
     */
    public static boolean isSimpleType(Class<?> type) {
        return SIMPLE_TYPES.contains(type);
    }

    public static Object convertPrimitive(Class<?> type, String value) {
        if (value == null) {
            return null;
        } else if (type == char.class || type == Character.class) {
            return value.length() > 0 ? value.charAt(0) : '\0';
        } else if (type == boolean.class || type == Boolean.class) {
            return Boolean.valueOf(value);
        }
        try {
            if (type == byte.class || type == Byte.class) {
                return Byte.valueOf(value);
            } else if (type == short.class || type == Short.class) {
                return Short.valueOf(value);
            } else if (type == int.class || type == Integer.class) {
                return Integer.valueOf(value);
            } else if (type == long.class || type == Long.class) {
                return Long.valueOf(value);
            } else if (type == float.class || type == Float.class) {
                return Float.valueOf(value);
            } else if (type == double.class || type == Double.class) {
                return Double.valueOf(value);
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return value;
    }


    /**
     * We only check boolean value at this moment.
     *
     * @param type
     * @param value
     * @return
     */
    public static boolean isTypeMatch(Class<?> type, String value) {
        if ((type == boolean.class || type == Boolean.class)
                && !("true".equals(value) || "false".equals(value))) {
            return false;
        }
        return true;
    }

    /**
     * Get all super classes from the specified type
     *
     * @param type         the specified type
     * @param classFilters the filters for classes
     * @return non-null read-only {@link Set}
     * @since 2.7.6
     */
    public static Set<Class<?>> getAllSuperClasses(Class<?> type, Predicate<Class<?>>... classFilters) {

        Set<Class<?>> allSuperClasses = new LinkedHashSet<>();

        Class<?> superClass = type.getSuperclass();
        while (superClass != null) {
            // add current super class
            allSuperClasses.add(superClass);
            superClass = superClass.getSuperclass();
        }

        return unmodifiableSet(filterAll(allSuperClasses, classFilters));
    }


    /**
     * Get all interfaces from the specified type
     *
     * @param type             the specified type
     * @param interfaceFilters the filters for interfaces
     * @return non-null read-only {@link Set}
     * @since 2.7.6
     */
    public static Set<Class<?>> getAllInterfaces(Class<?> type, Predicate<Class<?>>... interfaceFilters) {
        if (type == null || type.isPrimitive()) {
            return emptySet();
        }

        Set<Class<?>> allInterfaces = new LinkedHashSet<>();
        Set<Class<?>> resolved = new LinkedHashSet<>();
        Queue<Class<?>> waitResolve = new LinkedList<>();

        resolved.add(type);
        Class<?> clazz = type;
        while (clazz != null) {

            Class<?>[] interfaces = clazz.getInterfaces();

            if (isNotEmpty(interfaces)) {
                // add current interfaces
                Arrays.stream(interfaces)
                        .filter(resolved::add)
                        .forEach(cls -> {
                            allInterfaces.add(cls);
                            waitResolve.add(cls);
                        });
            }

            // add all super classes to waitResolve
            getAllSuperClasses(clazz)
                    .stream()
                    .filter(resolved::add)
                    .forEach(waitResolve::add);

            clazz = waitResolve.poll();
        }

        return filterAll(allInterfaces, interfaceFilters);
    }

    /**
     * Get all inherited types from the specified type
     *
     * @param type        the specified type
     * @param typeFilters the filters for types
     * @return non-null read-only {@link Set}
     * @since 2.7.6
     */
    public static Set<Class<?>> getAllInheritedTypes(Class<?> type, Predicate<Class<?>>... typeFilters) {
        // Add all super classes
        Set<Class<?>> types = new LinkedHashSet<>(getAllSuperClasses(type, typeFilters));
        // Add all interface classes
        types.addAll(getAllInterfaces(type, typeFilters));
        return unmodifiableSet(types);
    }


    /**
     * the semantics is same as {@link Class#isAssignableFrom(Class)}
     *
     * @param superType  the super type
     * @param targetType the target type
     * @return see {@link Class#isAssignableFrom(Class)}
     * @since 2.7.6
     */
    public static boolean isAssignableFrom(Class<?> superType, Class<?> targetType) {
        // any argument is null
        if (superType == null || targetType == null) {
            return false;
        }
        // equals
        if (Objects.equals(superType, targetType)) {
            return true;
        }
        // isAssignableFrom
        return superType.isAssignableFrom(targetType);
    }

    /**
     * Test the specified class name is present in the {@link ClassLoader}
     *
     * @param className   the name of {@link Class}
     * @param classLoader {@link ClassLoader}
     * @return If found, return <code>true</code>
     * @since 2.7.6
     */
    public static boolean isPresent(String className, ClassLoader classLoader) {
        try {
            forName(className, classLoader);
        } catch (Throwable ignored) { // Ignored
            return false;
        }
        return true;
    }

    /**
     * Resolve the {@link Class} by the specified name and {@link ClassLoader}
     *
     * @param className   the name of {@link Class}
     * @param classLoader {@link ClassLoader}
     * @return If can't be resolved , return <code>null</code>
     * @since 2.7.6
     */
    public static Class<?> resolveClass(String className, ClassLoader classLoader) {
        Class<?> targetClass = null;
        try {
            targetClass = forName(className, classLoader);
        } catch (Throwable ignored) { // Ignored
        }
        return targetClass;
    }

    /**
     * Is generic class or not?
     *
     * @param type the target type
     * @return if the target type is not null or <code>void</code> or Void.class, return <code>true</code>, or false
     * @since 2.7.6
     */
    public static boolean isGenericClass(Class<?> type) {
        return type != null && !void.class.equals(type) && !Void.class.equals(type);
    }
}
