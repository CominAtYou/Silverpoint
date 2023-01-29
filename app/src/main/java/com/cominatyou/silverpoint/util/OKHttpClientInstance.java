package com.cominatyou.silverpoint.util;

import okhttp3.OkHttpClient;

public class OKHttpClientInstance {
    private static final OkHttpClient client = new OkHttpClient();

    public static OkHttpClient getInstance() {
        return client;
    }
}
