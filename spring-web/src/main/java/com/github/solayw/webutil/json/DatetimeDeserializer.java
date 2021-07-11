package com.github.solayw.webutil.json;

public class DatetimeDeserializer extends AbstractDateDeserializer
{
    @Override
    protected String format() {
        return "yyyy-MM-dd HH:mm:ss";
    }
}
