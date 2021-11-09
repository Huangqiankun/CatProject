package com.hqk.catproject.http;

import com.hqk.catproject.BuildConfig;

public class UrlConfig {

    public static final String GET_ACCESS_TOKEN = BuildConfig.BASE_URL + "oauth/2.0/token";

    public static final String GET_ANIMAL = BuildConfig.BASE_URL + "rest/2.0/image-classify/v1/animal";
}
