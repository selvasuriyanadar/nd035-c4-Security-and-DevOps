package com.example.demo.security;

public class SecurityConstants {

	public static final String SECRET = "oursecretkey";
    public static final long EXPIRATION_TIME = 864_000_000; // 10 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/api/user/create";
    public static final String SWAGGER_URLS = "/swagger-ui/*";
    public static final String OPENAPI_URLS = "/v3/api-docs/*";
    public static final String SWAGGER_URL = "/swagger-ui.html";
    public static final String OPENAPI_URL = "/v3/api-docs";

}
