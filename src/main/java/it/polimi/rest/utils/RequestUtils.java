package it.polimi.rest.utils;

import it.polimi.rest.exceptions.InternalErrorException;
import spark.Request;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestUtils {

    private RequestUtils() {

    }

    public static Map<String, String> bodyParams(Request request) {
        Map<String, String> result = new HashMap<>();

        String body = request.body();

        if (body != null) {
            for (String entry : body.split("&")) {
                String[] vals = entry.split("=");

                if (vals.length == 2) {
                    String param = decode(vals[0]).trim();
                    String value = decode(vals[1]).trim();

                    if (!param.isEmpty() && !value.isEmpty()) {
                        result.put(param, value);
                    }
                }
            }
        }

        return Collections.unmodifiableMap(result);
    }

    public static String decode(String value) {
        if (value == null) {
            return null;
        }

        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.toString());

        } catch (UnsupportedEncodingException e) {
            throw new InternalErrorException(e.getMessage());
        }
    }

}
