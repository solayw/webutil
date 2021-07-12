package com.github.solayw.webutil.jpa;

import javax.persistence.AttributeConverter;

public class ConverterRepository
{
    <X,Y> AttributeConverter<X,Y> findByConverterType(Class aClazz) {
        return null;
    }

    <X,Y> AttributeConverter<X,Y> findByAttributeType(Class aClazz) {
        return null;
    }
}
