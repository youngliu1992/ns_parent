package com.creditease.ns.dispatcher.convertor.json;

import com.creditease.ns.log.Log;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang.StringUtils;

public class JSONConvertor
{
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String toJSON(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            Log.logError("Object TO JSON:{} 出现异常:{}", e);
        }return "";
    }

    public static <T> T toObject(String json, Class<T> clazz)
    {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            Log.logError("JSON TO OBJECT :" + json + " 出现异常:{}", e);
        }return null;
    }

    public static Map<String, String> jsonToMap(String json)
    {
        try {
            Map input = (Map)objectMapper.readValue(json, new TypeReference()
            {
            });
            Map output = new HashMap(input.size());
            StringWriter writer = new StringWriter();
            StringBuffer buf = writer.getBuffer();
            for (Map.Entry entry : input.entrySet()) {
                JsonGenerator gen = new JsonFactory(objectMapper).createGenerator(writer); Throwable localThrowable2 = null;
                try { gen.writeObject(entry.getValue());
                }
                catch (Throwable localThrowable1)
                {
                    localThrowable2 = localThrowable1;
                } finally {
                    if (gen != null) if (localThrowable2 != null) try { gen.close(); } catch (Throwable x2) {
                        localThrowable2.addSuppressed(x2);
                    } else gen.close();
                }
                output.put(entry.getKey(), StringUtils.stripEnd(StringUtils.stripStart(buf.toString(), "\""), "\""));
                buf.setLength(0);
            }
            return output;
        } catch (IOException e) {
            Log.logError("JSON TO MAP :" + json + " 出现异常:{}", e);
        }return null;
    }

    public static boolean isValidJSON(String json)
    {
        boolean valid = false;
        try {
            JsonParser parser = objectMapper.getFactory().createParser(json);

            while (parser.nextToken() != null);
            valid = true;
        }
        catch (JsonParseException jpe)
        {
        }
        catch (IOException ioe) {
        }
        return valid;
    }
}