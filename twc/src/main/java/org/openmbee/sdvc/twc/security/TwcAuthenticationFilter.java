package org.openmbee.sdvc.twc.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmbee.sdvc.core.utils.RestUtils;
import org.openmbee.sdvc.twc.config.TwcConfig;
import org.openmbee.sdvc.twc.constants.TwcConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;


public class TwcAuthenticationFilter extends BasicAuthenticationFilter {


    @Autowired
    private TwcUserDetailsService userDetailsService;

	@Autowired
	private TwcConfig twcConfig;


	public TwcAuthenticationFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
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
