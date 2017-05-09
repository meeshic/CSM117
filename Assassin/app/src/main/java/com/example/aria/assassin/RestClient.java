package com.example.aria.assassin;

import com.loopj.android.http.*;

public class RestClient {
    private static final String BASE_URL = "https://api.twitter.com/1/";

    public static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}

