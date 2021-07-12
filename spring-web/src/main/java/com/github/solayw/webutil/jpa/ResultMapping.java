package com.github.solayw.webutil.jpa;

import com.github.solayw.webutil.ReflectionUtil;

import org.hibernate.transform.ResultTransformer;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.persistence.AttributeConverter;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Transient;

public class ResultMapping implements ResultTransformer
{


    private Class clazz;
    private boolean includeTransient;
    private Supplier constructor;
    private HashMap<String, Function> mappers;
    private Function<String, String> propertyAliasMapping;


    public ResultMapping(Class clazz, boolean includeTransient, Supplier constructor, ConverterRepository repository) {
        this.clazz = clazz;
        this.includeTransient = includeTransient;
        this.constructor = constructor;
        List<Field> fields = ReflectionUtil.fields(clazz, false, true, false, true, includeTransient,
                it -> !it.isAnnotationPresent(Transient.class));

        mappers = new HashMap<>();
        for (Field f : fields) {
            f.setAccessible(true);
            final Class<?> fieldType = f.getType();
            AttributeConverter converter = null;
            final Convert c = f.getAnnotation(Convert.class);
            if (c != null) {
                converter = repository.findByConverterType(c.converter());
            } else {
                converter = repository.findByAttributeType(fieldType);
            }

            AttributeConverter _converter = converter;
            Function func = it -> {
                if(it == null) {
                    return null;
                }
                return _converter == null ? it : _converter.convertToEntityAttribute(it);
            };
            final Column cc = f.getAnnotation(Column.class);
            if (cc != null && !cc.name().isEmpty()) {
                mappers.put(cc.name(), func);
            } else {
                mappers.put(propertyAliasMapping.apply(f.getName()), func);
                mappers.put(f.getName(), func);
            }
        }

    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        return null;
    }

    @Override
    public List transformList(List collection) {
        return collection;
    }







}
