package com.example.aria.assassin.RestClient;

import com.loopj.android.http.*;

public class RestClient {
    private static final String BASE_URL = "https://assassin-server-csm117.herokuapp.com/";

    public static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}

