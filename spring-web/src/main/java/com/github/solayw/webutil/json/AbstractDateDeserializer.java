package com.github.solayw.webutil.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public abstract class AbstractDateDeserializer extends JsonDeserializer<Long>
{
    @Override
    public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        switch (p.currentToken()) {
            case VALUE_NUMBER_INT:
                return p.getLongValue();
            case VALUE_STRING:
                final String s = p.getValueAsString();
                if(s.isEmpty()) {
                    return null;
                }
                try {
                    return new SimpleDateFormat(format()).parse(s).getTime();
                }catch (ParseException ex) {
                    throw InvalidFormatException.from(p, Long.class, String.format("[%s] does not match [%s]", s, format()));
                }
            default:
                throw InvalidFormatException.from(p, Long.class, String.format("data does not match [%s]", format()));
        }
    }

    protected abstract String format();
}