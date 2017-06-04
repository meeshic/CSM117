package com.csm117.astar.assassin.RestClient;

public class RestClient {
    private static final String BASE_URL = "https://assassin-server-csm117.herokuapp.com/";

    public static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}

