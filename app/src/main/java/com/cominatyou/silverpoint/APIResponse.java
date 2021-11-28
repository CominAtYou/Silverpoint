package com.cominatyou.silverpoint;

import org.json.JSONObject;

public class APIResponse {
    private static JSONObject status = null;

    public static void setStatus(JSONObject status) {
        APIResponse.status = status;
    }

    public static JSONObject getStatus() {
        return status;
    }
}
