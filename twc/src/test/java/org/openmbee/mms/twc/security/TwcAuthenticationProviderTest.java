package org.openmbee.mms.twc.security;

import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestResponse;
import org.junit.Before;
import org.junit.Test;
import org.openmbee.mms.twc.TeamworkCloud;
import org.openmbee.mms.twc.constants.TwcConstants;
import org.openmbee.mms.twc.utilities.RestUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TwcAuthenticationProviderTest {

    private final String token = "my token";
    private final String userName = "the user";

    private TeamworkCloud twc;
    private RestUtils restUtils;
    private RestTemplate mockRestTemplate;

    @Before
    public void setup() {
        twc = new TeamworkCloud();
        twc.setAliases(Collections.emptyList());
        twc.setUrl("twc.domain.com");
        twc.setPort("8111");
        twc.setProtocol("http");

        restUtils = mock(RestUtils.class);
        mockRestTemplate = mock(RestTemplate.class);

        when(restUtils.getRestTemplate()).thenReturn(mockRestTemplate);
    }

    @Test
    public void testSuccessfulAuthentication(){

        ResponseEntity<RestResponse> responseEntity = mock(ResponseEntity.class);

        when(restUtils.getCookieValue(responseEntity, TwcConstants.TWCCURRENTUSER)).thenReturn(userName);

        TwcAuthenticationProvider twcAuthenticationProvider = new TwcAuthenticationProvider(restUtils, twc);

        when(mockRestTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(RestResponse.class)))
            .thenAnswer((invocation) -> {
                    Object[] args = invocation.getArguments();

                    assertEquals("http://twc.domain.com:8111/osmc/login", args[0]);

                    HttpEntity<RestRequest> req = (HttpEntity<RestRequest>)args[2];
                    assertEquals(token, req.getHeaders().get(RestUtils.AUTHORIZATION).get(0));

                    return responseEntity;
            });

        String user = twcAuthenticationProvider.getAuthentication(token);
        assertEquals(userName, user);
    }

    @Test
    public void testAuthenticationFailure() {
        ResponseEntity<RestResponse> responseEntity = mock(ResponseEntity.class);

        when(restUtils.getCookieValue(responseEntity, TwcConstants.TWCCURRENTUSER)).thenReturn(null);

        TwcAuthenticationProvider twcAuthenticationProvider = new TwcAuthenticationProvider(restUtils, twc);

        when(mockRestTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(RestResponse.class)))
            .thenAnswer((invocation) -> {
                Object[] args = invocation.getArguments();

                assertEquals("http://twc.domain.com:8111/osmc/login", args[0]);

                HttpEntity<RestRequest> req = (HttpEntity<RestRequest>)args[2];
                assertEquals(token, req.getHeaders().get(RestUtils.AUTHORIZATION).get(0));

                return responseEntity;
            });

        String user = twcAuthenticationProvider.getAuthentication(token);
        assertNull(user);
    }

    @Test
    public void testAuthenticationException() {
        ResponseEntity<RestResponse> responseEntity = mock(ResponseEntity.class);

        when(restUtils.getCookieValue(responseEntity, TwcConstants.TWCCURRENTUSER)).thenReturn(null);

        TwcAuthenticationProvider twcAuthenticationProvider = new TwcAuthenticationProvider(restUtils, twc);

        when(mockRestTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(RestResponse.class)))
            .thenAnswer((invocation) -> {
                throw new RuntimeException("Test Exception -- should be caught");
            });

        String user = twcAuthenticationProvider.getAuthentication(token);
        assertNull(user);
    }

    @Test
    public void testNullToken() {
        TwcAuthenticationProvider twcAuthenticationProvider = new TwcAuthenticationProvider(restUtils, twc);
        String user = twcAuthenticationProvider.getAuthentication(null);
        assertNull(user);
    }

}
