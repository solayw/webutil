package com.github.solayw.webutil;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;

import sun.misc.Unsafe;

/**
 *  基于unSafe实现的类copy，默认会拷贝private成员
 */
public class UnSafePropertyCopy<S, T>
{
   private static final Unsafe unsafe;
   private static final CopyOptions defaultOptions = new CopyOptions();
   static {
       try {
           Field _f = Unsafe.class.getDeclaredField("theUnsafe");
           _f.setAccessible(true);
           unsafe = (Unsafe)_f.get(null);
       }catch (Exception e) {
           throw new Error("unsafe unavailable");
       }
   }

   private static Map<String, Field> fields(Class type) {
       Map<String, Field> res = new HashMap<>();
       while (type != Object.class) {
           for (Field x : type.getDeclaredFields()) {
               if(!Modifier.isStatic(x.getModifiers())) {
                   res.put(x.getName(), x);
               }
           }
           type = type.getSuperclass();
       }
       return res;
   }

   public static <S,T> UnSafePropertyCopy<S, T> of(Class<S> source, Class<T> target) {
       return new UnSafePropertyCopy<>(source, target);
   }
    public UnSafePropertyCopy(Class<S> source, Class<T> target) {
        this(source, target, defaultOptions);
    }

   public UnSafePropertyCopy(Class<S> source, Class<T> target, CopyOptions copyOptions) {
       Objects.requireNonNull(copyOptions);
       BiPredicate<Field, Field> filter = copyOptions.filter;
       Map<String, Field> s = fields(source);
       Map<String, Field> t  = fields(target);
       if(s.isEmpty()) {
           throw new IllegalArgumentException("type  " + source.getName() + " has no filed to copy from");
       }
       if(t.isEmpty()) {
           throw new IllegalArgumentException("type  " + target.getName() + " has no filed to copy to");
       }
       ArrayList<Field> sf_list = new ArrayList<Field>();
       ArrayList<Field> tf_list = new ArrayList<Field>();
       s.forEach((name, sf) -> {
           Field tf = t.get(name);
            if(tf == null) {
                return;
            }
            if(filter != null && !filter.test(sf, tf)) {
                return;
            }
           if(!tf.getType().isAssignableFrom(sf.getType())) {
               if(copyOptions.reportOnTypeDisMatch) {
                   throw new IllegalArgumentException(target.getName() + ":" + sf.getName() + " cannot be assigned from " + source.getName() + ":" + tf.getName());
               } else {
                   return;
               }

           }
           if(Modifier.isFinal(tf.getModifiers())) {
               if(copyOptions.reportOnTypeDisMatch) {
                   throw new IllegalArgumentException(target.getName() + ":" + tf.getName() + " is final");
               } else {
                   return;
               }
           }
           sf_list.add(sf);
           tf_list.add(tf);
       });
       int n =  sf_list.size();
       this._source = new long[n];
       this._target = new long[n];
       this._type = new byte[n];
       for (int i = 0; i < n; i++) {
           Class<?> a = sf_list.get(i).getType();
           byte at;
           if(a == boolean.class) {
               at = 0;
           } else if(a == byte.class) {
               at = 1;
           } else if(a == char.class) {
               at = 2;
           } else if(a == short.class) {
               at = 3;
           } else if(a == int.class) {
               at = 4;
           } else if(a == long.class) {
               at = 5;
           } else if(a == float.class) {
               at = 6;
           } else if(a == double.class) {
               at = 7;
           } else {
               at = 8;
           }
           this._source[i] = unsafe.objectFieldOffset(sf_list.get(i));
           this._target[i] = unsafe.objectFieldOffset(tf_list.get(i));
           this._type[i] = at;
       }
   }


   private final long[] _source;
   private final long[] _target;
   private final byte[] _type;



   public void copy(S source, T target) {
       int n = _source.length;
       Unsafe _unsafe = unsafe;
       for (int i = 0; i < n; i++) {
           long sourceOffset = _source[i];
           long targetOffset = _target[i];
           switch (_type[i]) {
               case 0: _unsafe.putBoolean(target, targetOffset, _unsafe.getBoolean(source, sourceOffset)); break;
               case 1: _unsafe.putByte(target, targetOffset, _unsafe.getByte(source, sourceOffset)); break;
               case 2: _unsafe.putChar(target, targetOffset, _unsafe.getChar(source, sourceOffset)); break;
               case 3: _unsafe.putShort(target, targetOffset, _unsafe.getShort(source, sourceOffset)); break;
               case 4: _unsafe.putInt(target, targetOffset, _unsafe.getInt(source, sourceOffset)); break;
               case 5: _unsafe.putLong(target, targetOffset, _unsafe.getLong(source, sourceOffset)); break;
               case 6: _unsafe.putFloat(target, targetOffset, _unsafe.getFloat(source, sourceOffset)); break;
               case 7: _unsafe.putDouble(target, targetOffset, _unsafe.getDouble(source, sourceOffset)); break;
               case 8: _unsafe.putObject(target, targetOffset, _unsafe.getObject(source, sourceOffset)); break;

           }
       }
   }
}
