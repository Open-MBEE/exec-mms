package org.openmbee.sdvc.core.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RestUtils {

    public static final String AUTHORIZATION = HttpHeaders.AUTHORIZATION;
    public static final String SET_COOKIE = "Set-Cookie";

    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    public String getCookieValue(ResponseEntity<?> responseEntity, String cookieName) {


        if (responseEntity == null || responseEntity.getHeaders() == null)
            return null;

        Pattern pattern = Pattern.compile(cookieName + "=([^;]+)(;.*)?");

        for(String cookie : responseEntity.getHeaders().get(SET_COOKIE)) {
            Matcher matcher = pattern.matcher(cookie);
            if (matcher.matches()) {
                return matcher.group(1);
            }
        }

        return null;
    }
}
