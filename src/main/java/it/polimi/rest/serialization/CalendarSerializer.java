package it.polimi.rest.serialization;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class CalendarSerializer implements JsonSerializer<Calendar> {

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    @Override
    public JsonElement serialize(Calendar src, Type typeOfSrc, JsonSerializationContext context) {
        Date date = src.getTime();
        sdf.setTimeZone(TimeZone.getTimeZone("CET"));
        return new JsonPrimitive(sdf.format(date));
    }

}
