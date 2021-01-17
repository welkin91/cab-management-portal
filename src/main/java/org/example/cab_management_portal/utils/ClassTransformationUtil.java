package org.example.cab_management_portal.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
public class ClassTransformationUtil {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static <T> T fromString(String string, Class<T> clazz) {

        try {
            T response = OBJECT_MAPPER.readValue(string, clazz);

            return response;
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "The given string value: " + string + " cannot be transformed to Json object", e);
        }
    }

    public static <T> List<T> fromStringArray (String input, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(input, new TypeReference<List<T>>(){});
        }
        catch (Exception e) {
            throw new IllegalArgumentException(
                    "The given string value: " + input + " cannot be transformed to Json object", e);
        }
    }

    public static String getJsonStringFromFile(String filePath) throws IOException {
        InputStream in = ClassTransformationUtil.class.getClassLoader().getResourceAsStream(filePath);
        return convertStreamToString(in);
    }

    private static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static String toString(Object value) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "The given Json object value: " + value + " cannot be transformed to a String");
        }
    }
}
