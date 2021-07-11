package com.github.solayw.webutil.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatetimeSerializer extends JsonSerializer<Long>
{
    @Override
    public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String str = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(value));
        gen.writeString(str);
    }


}
