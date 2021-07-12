package com.github.solayw.webutil.jpa;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@FunctionalInterface
public interface Extractor<T>
{

    static HashMap<Class, Extractor> init() {
        HashMap<Class, Extractor> extractors = new HashMap<>();
        extractors.put(Integer.class, ResultSet::getInt);
        extractors.put(int.class, ResultSet::getInt);
        extractors.put(Long.class, ResultSet::getLong);
        extractors.put(long.class, ResultSet::getLong);
        extractors.put(Float.class, ResultSet::getFloat);
        extractors.put(float.class, ResultSet::getLong);
        extractors.put(Double.class, ResultSet::getDouble);
        extractors.put(double.class, ResultSet::getDouble);
        extractors.put(BigDecimal .class, ResultSet::getBigDecimal);
        extractors.put(Boolean.class, ResultSet::getBoolean);
        extractors.put(boolean.class, ResultSet::getBoolean);
        extractors.put(String.class, ResultSet::getString);
        extractors.put(java.sql.Date.class, ResultSet::getDate);
        return extractors;
    }

    HashMap<Class, Extractor> extractors = init();

    static <T> Extractor<T> compatibleExtractor(Class<T> clazz) {

        Extractor extractor = extractors.get(clazz);
        if(extractor != null) {
            return extractor;
        }
        for (Map.Entry<Class, Extractor> entry : extractors.entrySet()) {
            if(clazz.isAssignableFrom(entry.getKey())) {
                return entry.getValue();
            }
        }
        return (rs, columnIndex) -> rs.getObject(columnIndex, clazz);
    }



    default T extract(ResultSet rs, int idx) throws SQLException {
        T obj = doExtract(rs, idx);
        if(rs.wasNull()) {
            return null;
        }
        return obj;
    }
    T doExtract(ResultSet rs, int idx) throws SQLException;

}
