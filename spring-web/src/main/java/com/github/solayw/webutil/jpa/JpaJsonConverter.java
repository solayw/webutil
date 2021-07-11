package com.github.solayw.webutil.jpa;


import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.persistence.AttributeConverter;

import lombok.SneakyThrows;

/**
 *  jpa json 映射  注意加上@EqualsAndHashCode
 */
public class JpaJsonConverter<T> implements AttributeConverter<T, String>
{
    public static Gson gson = new GsonBuilder()
            .addSerializationExclusionStrategy(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    final Expose anno = f.getAnnotation(Expose.class);
                    return anno != null && !anno.serialize();
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }
            }).create();

    private final Type type;

    {
        Class _clazz = this.getClass();
        while (_clazz.getSuperclass() != JpaJsonConverter.class) {
            _clazz = _clazz.getSuperclass();
        }
        type =((ParameterizedType)_clazz.getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    @SneakyThrows
    public String convertToDatabaseColumn(T attribute) {
        return gson.toJson(attribute);
    }

    @Override
    @SneakyThrows
    public T convertToEntityAttribute(String dbData) {
        if(dbData == null || dbData.length() == 0) {
            return null;
        }
        return gson.fromJson(dbData, type);
    }
}
