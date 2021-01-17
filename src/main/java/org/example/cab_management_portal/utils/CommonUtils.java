package org.example.cab_management_portal.utils;

import java.io.IOException;
import java.io.InputStream;

public class CommonUtils {

    public static String getJsonStringFromFile(String filePath) throws IOException {
        InputStream in = CommonUtils.class.getClassLoader().getResourceAsStream(filePath);
        return convertStreamToString(in);
    }

    private static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static boolean isEmpty(String str) {
        if(str == null || str.length() == 0) {
            return true;
        }

        return false;
    }
}
