package com.serverless;

import java.util.HashMap;
import java.util.Map;

public class Headers {

    public static Map<String, String> headers = initHeaders();

    private static Map<String, String> initHeaders() {
        final HashMap<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Credentials", "true");
        headers.put("X-Powered-By", "AWS Lambda & Serverless");
        return headers;
    }

}
