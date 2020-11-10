package org.openmbee.mms.twc.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmbee.mms.twc.constants.TwcConstants;
import org.openmbee.mms.twc.utilities.RestUtils;
import org.openmbee.mms.twc.config.TwcConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;


public class TwcAuthenticationFilter extends OncePerRequestFilter {

    private TwcUserDetailsService userDetailsService;
	private TwcConfig twcConfig;

    public TwcUserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    @Autowired
    public void setUserDetailsService(TwcUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public TwcConfig getTwcConfig() {
        return twcConfig;
    }

    @Autowired
    public void setTwcConfig(TwcConfig twcConfig) {
        this.twcConfig = twcConfig;
    }

    @Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {


		try {
		    String associatedTWC = request.getHeader(TwcConstants.ASSOCIATED_TWC_HEADER);
		    String token = request.getHeader(RestUtils.AUTHORIZATION);
		    TwcAuthenticationProvider twcAuthProvider = twcConfig.getAuthNProvider(associatedTWC);
		    if(twcAuthProvider != null) {
                String authenticatedUser = twcAuthProvider.getAuthentication(token);
                if(authenticatedUser != null) {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(authenticatedUser);
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null,
                            userDetails.getAuthorities());
                    authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
			e.printStackTrace();
		} finally {
            filterChain.doFilter(request, response);
        }
	}
}
