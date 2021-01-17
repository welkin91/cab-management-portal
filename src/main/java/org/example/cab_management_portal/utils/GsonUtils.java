package org.example.cab_management_portal.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GsonUtils {

    public static final Gson GSON = new GsonBuilder().create();
    public static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();

    public static String toString(Object o) {
        return GSON.toJson(o);
    }

    public static String toPrettyString(Object o) {
        return PRETTY_GSON.toJson(o);
    }
}
