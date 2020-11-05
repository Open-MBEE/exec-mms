package org.openmbee.mms.twc.utilities;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RestUtilsTest {

    @Test
    public void testGetCookieValue() {

        ResponseEntity responseEntity = mock(ResponseEntity.class);
        HttpHeaders headers = mock(HttpHeaders.class);

        when(responseEntity.getHeaders()).thenReturn(headers);
        when(headers.get(RestUtils.SET_COOKIE)).thenReturn(
            List.of("twc-rest-current-user=theuser; Path=/osmc; Expires=Mon, 10 Feb 2020 16:59:21 GMT",
                "twc-rest-session-id=9235a8; Path=/osmc; Expires=Mon, 10 Feb 2020 16:59:21 GMT" ));


        RestUtils restUtils = new RestUtils();
        assertEquals("theuser", restUtils.getCookieValue(responseEntity, "twc-rest-current-user"));
    }

    @Test
    public void testGetCookieValue2() {

        ResponseEntity responseEntity = mock(ResponseEntity.class);
        HttpHeaders headers = mock(HttpHeaders.class);

        when(responseEntity.getHeaders()).thenReturn(headers);
        when(headers.get(RestUtils.SET_COOKIE)).thenReturn(
            List.of("twc-rest-current-user=theuser; Path=/osmc; Expires=Mon, 10 Feb 2020 16:59:21 GMT",
                "twc-rest-session-id=9235a8; Path=/osmc; Expires=Mon, 10 Feb 2020 16:59:21 GMT" ));


        RestUtils restUtils = new RestUtils();
        assertEquals("9235a8", restUtils.getCookieValue(responseEntity, "twc-rest-session-id"));
    }

    @Test
    public void testNullResponse() {
        RestUtils restUtils = new RestUtils();
        assertNull(restUtils.getCookieValue(null, "something"));
    }

    @Test
    public void testNullHeaders() {
        ResponseEntity responseEntity = mock(ResponseEntity.class);
        when(responseEntity.getHeaders()).thenReturn(null);

        RestUtils restUtils = new RestUtils();
        assertNull(restUtils.getCookieValue(null, "something"));
    }

    @Test
    public void testMissingCookies() {

        ResponseEntity responseEntity = mock(ResponseEntity.class);
        HttpHeaders headers = mock(HttpHeaders.class);

        when(responseEntity.getHeaders()).thenReturn(headers);
        when(headers.get(RestUtils.SET_COOKIE)).thenReturn(
            List.of("twc-rest-current-user=theuser; Path=/osmc; Expires=Mon, 10 Feb 2020 16:59:21 GMT",
                "twc-rest-session-id=9235a8; Path=/osmc; Expires=Mon, 10 Feb 2020 16:59:21 GMT" ));


        RestUtils restUtils = new RestUtils();
        assertNull(restUtils.getCookieValue(responseEntity, "something"));
    }

    @Test
    public void testMissingCookies2() {

        ResponseEntity responseEntity = mock(ResponseEntity.class);
        HttpHeaders headers = mock(HttpHeaders.class);

        when(responseEntity.getHeaders()).thenReturn(headers);
        when(headers.get(RestUtils.SET_COOKIE)).thenReturn(List.of( ));

        RestUtils restUtils = new RestUtils();
        assertNull(restUtils.getCookieValue(responseEntity, "something"));
    }
}